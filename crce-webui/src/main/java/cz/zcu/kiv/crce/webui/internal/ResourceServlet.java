package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.CompatibilityVersionComparator;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.webui.internal.bean.Category;

public class ResourceServlet extends HttpServlet {

    private static final long serialVersionUID = -4218424299866417104L;
    
    private static final Logger logger = LoggerFactory.getLogger(ResourceServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String source = (String) req.getSession().getAttribute("source");
        logger.debug("Resource servlet POST source: {}", source);

        //if form was submit, set session parameters{
        if ("yes".equalsIgnoreCase(req.getParameter("showStoreTag"))) {
            req.getSession().setAttribute("showStoreTag", "yes");
            logger.debug("showStoreTag session attribute set to yes");
        } else {
            req.getSession().setAttribute("showStoreTag", "no");
            logger.debug("showStoreTag session attribute set to no");
        }

        if ("yes".equalsIgnoreCase(req.getParameter("showBufferTag"))) {
            req.getSession().setAttribute("showBufferTag", "yes");
            logger.debug("showBufferTag session attribute set to yes");
        } else {
            req.getSession().setAttribute("showBufferTag", "no");
            logger.debug("showBufferTag session attribute set to no");
        }

        if ("upload".equals(source) || "commit".equals(source)) {
            doGet(req, resp);
        } else if (req.getParameter("filter") != null) {
            String filter = req.getParameter("filter");
            fillSession(source, req, filter);
            req.getRequestDispatcher("jsp/" + source + ".jsp").forward(req, resp);
        } else {
            doGet(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        String link = null;

        if (req.getParameter("link") != null && req.getParameter("link") instanceof String) {
            link = req.getParameter("link");
        }
        
        try {
            if (fillSession(link, req, null)) {
                //resp.sendRedirect("jsp/"+link+".jsp");
                req.getRequestDispatcher("jsp/" + link + ".jsp").forward(req, resp);
            } else {
                logger.debug("Default forward");
                req.getRequestDispatcher("resource?link=store").forward(req, resp);
            }

        } catch (ServletException e) {
            logger.warn("Can't forward: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Can't forward", e);
        }
    }

    public static void cleanSession(HttpSession session) {
        session.removeAttribute("resources");
        session.removeAttribute("plugins");
        session.removeAttribute("store");
    }

    public static void setError(HttpSession session, boolean success, String message) {
        session.setAttribute("success", success);
        session.setAttribute("message", message);
    }

    private boolean fillSession(String link, HttpServletRequest req, String filter) {

        String errorMessage = filter + " is not a valid filter";
        HttpSession session = req.getSession();
        cleanSession(session);
        if (link == null) {
            return false;
        }
        switch (link) {
            case "buffer":
                Resource[] buffer;
                if (filter == null) {
                    buffer = Activator.instance().getBuffer(req).getRepository().getResources();
                } else {
                    try {
                        buffer = Activator.instance().getBuffer(req).getRepository().getResources(filter);
                    } catch (InvalidSyntaxException e) {
                        setError(session, false, errorMessage);
                        buffer = Activator.instance().getBuffer(req).getRepository().getResources();
                    }
                }
                session.setAttribute("buffer", buffer);
                return true;
                
            case "plugins":
                Plugin[] plugins;
                if (filter == null) {
                    plugins = Activator.instance().getPluginManager().getPlugins();
                } else {
                    plugins = Activator.instance().getPluginManager().getPlugins(Plugin.class, filter);
                }
                session.setAttribute("plugins", plugins);
                return true;
                
            case "store":
                Resource[] store;
                if (filter == null) {
                    store = Activator.instance().getStore().getRepository().getResources();
                } else {
                    try {
                        store = Activator.instance().getStore().getRepository().getResources(filter);
                    } catch (InvalidSyntaxException e) {
                        setError(session, false, errorMessage);
                        store = Activator.instance().getStore().getRepository().getResources();
                    }
                }
                session.setAttribute("store", store);
                return true;
                
            case "tags":
                Resource[] resources = prepareResourceArray(req, filter);
                ArrayList<Category> categoryList = prepareCategoryList(resources);
                ArrayList<Resource> filteredResourceList;

                String selectedCategory;
                if (req.getParameter("tag") != null && req.getParameter("tag") instanceof String) {
                    selectedCategory = req.getParameter("tag");

                    filteredResourceList = filterResurces(selectedCategory, resources);

                } else {
                    filteredResourceList = null;
                }

                session.setAttribute("resources", filteredResourceList);
                session.setAttribute("categoryList", categoryList);

                return true;

            case "compatibility":
                String name = req.getParameter("name");
                String version = req.getParameter("version");

                List<Compatibility> lower = null;
                List<Compatibility> upper = null;

                if(name == null || name.isEmpty() || version == null || version.isEmpty()) {
                    session.setAttribute("nodata", true);
                    return true;
                }
                session.setAttribute("nodata", false);

                String resFilter = "(&(symbolicName=" + name + ")(version=" + version + "))";
                try {
                    Resource res[] = Activator.instance().getStore().getRepository().getResources(resFilter);
                    if(res.length > 0) {
                        lower = Activator.instance().getCompatibilityService().listLowerCompatibilities(res[0]);
                        Collections.sort(lower, CompatibilityVersionComparator.getBaseComparator());

                        upper = Activator.instance().getCompatibilityService().listUpperCompatibilities(res[0]);
                        Collections.sort(upper, CompatibilityVersionComparator.getUpperComparator());

                    }
                } catch (InvalidSyntaxException e) {
                    logger.error("Invalid syntax when searching for compatibility resource using filter: {}", resFilter);
                    return false;
                }

                session.setAttribute("pivotName", name);
                session.setAttribute("pivotVersion", version);
                session.setAttribute("lower", lower);
                session.setAttribute("upper", upper);

                return true;
                
            default:
                return false;
        }
    }

    /**
     * Prepare resource array. Resource array is created from store and buffer. Store resources are added, if request parameter
     * <code>store</code> is "yes". Buffer resources are added, if request parameter
     * <code>buffer</code> is "yes".
     *
     * @param req request with parameters
     * @param filter possible filter of resources
     * @return array of resources
     */
    private Resource[] prepareResourceArray(HttpServletRequest req, String filter) {
        String errorMessage = filter + " is not a valid filter";
        HttpSession session = req.getSession();
        Resource[] storeResources, bufferResources, resources;
        storeResources = new Resource[0];
        bufferResources = new Resource[0];

        String sessStoreAtr = (String) session.getAttribute("showStoreTag");
        String sessBufferAtr = (String) session.getAttribute("showBufferTag");

        if ("yes".equalsIgnoreCase(sessStoreAtr)) {
            if (filter == null) {
                storeResources = Activator.instance().getStore().getRepository().getResources();
            } else {
                try {
                    storeResources = Activator.instance().getStore().getRepository().getResources(filter);
                } catch (InvalidSyntaxException e) {
                    logger.warn("Invalid syntax", e);
                    setError(session, false, errorMessage);
                    storeResources = Activator.instance().getStore().getRepository().getResources();
                }
            }
        }

        if ("yes".equals(sessBufferAtr)) {
            if (filter == null) {
                bufferResources = Activator.instance().getBuffer(req).getRepository().getResources();
            } else {
                try {
                    bufferResources = Activator.instance().getBuffer(req).getRepository().getResources(filter);
                } catch (InvalidSyntaxException e) {
                    logger.warn("Invalid syntax", e);
                    setError(session, false, errorMessage);
                    bufferResources = Activator.instance().getBuffer(req).getRepository().getResources();
                }
            }
        }

        //merge two resources arrays
        resources = Arrays.copyOf(storeResources, storeResources.length + bufferResources.length);
        System.arraycopy(bufferResources, 0, resources, storeResources.length, bufferResources.length);

        return resources;
    }

    /**
     * Prepare list of categories (tags) that are on all resources in the store. Category is represented by name and the number of
     * occurrences.
     *
     * @param resources - all resources from the store
     * @return array list of categories
     */
    private ArrayList<Category> prepareCategoryList(Resource[] resources) {

        HashMap<String, Integer> categoryMap = new HashMap<>();

        for (Resource resource : resources) {
            String[] categories = resource.getCategories();
            for (String category : categories) {
                if (categoryMap.containsKey(category)) {
                    //category is already contained, increase count
                    categoryMap.put(category, new Integer(categoryMap.get(category).intValue() + 1));
                } else {
                    //add new category
                    categoryMap.put(category, new Integer(1));
                }
            }
        }

        ArrayList<Category> categoryList = new ArrayList<>();
        Set<String> categorySet = categoryMap.keySet();

        /*Get categories from map to list*/
        for (String category : categorySet) {
            Category newCategory = new Category(category);
            newCategory.setCount(categoryMap.get(category));
            categoryList.add(newCategory);
        }

        //sort category list
        Collections.sort(categoryList);

        return categoryList;


    }

    /**
     * Filter resources according some category. Output list contains only resources that have required category.
     *
     * @param filterCategory required category
     * @param resources resources
     * @return filtered resources list
     */
    private ArrayList<Resource> filterResurces(String filterCategory, Resource[] resources) {
        ArrayList<Resource> filteredResourceList = new ArrayList<>();
        for (Resource resource : resources) {
            String[] categories = resource.getCategories();
            for (String category : categories) {
                if (category.equals(filterCategory)) {
                    filteredResourceList.add(resource);
                    break;
                }
            }
        }

        return filteredResourceList;
    }
}
