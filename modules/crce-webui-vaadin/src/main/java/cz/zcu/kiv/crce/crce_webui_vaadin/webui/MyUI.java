package cz.zcu.kiv.crce.crce_webui_vaadin.webui;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.zcu.kiv.crce.crce_webui_vaadin.outer.CentralMavenForm;
import cz.zcu.kiv.crce.crce_webui_vaadin.outer.CheckMavenIndexForm;
import cz.zcu.kiv.crce.crce_webui_vaadin.outer.DefinedMavenForm;
import cz.zcu.kiv.crce.crce_webui_vaadin.outer.LoadFileForm;
import cz.zcu.kiv.crce.crce_webui_vaadin.outer.LocalMavenForm;
import cz.zcu.kiv.crce.crce_webui_vaadin.repository.BufferForm;
import cz.zcu.kiv.crce.crce_webui_vaadin.repository.PluginsForm;
import cz.zcu.kiv.crce.crce_webui_vaadin.repository.StoreForm;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@SuppressWarnings("serial")
@Theme("mytheme")
@Push
public class MyUI extends UI {
	private Panel head = new Panel();
	private Panel body = new Panel();
	private Panel footer = new Panel();
	private LocalMavenForm localMavenForm;
	private CentralMavenForm centralMavenForm;
	private CheckMavenIndexForm checkMavenIndexFrom;
	private DefinedMavenForm definedMavenForm;
	private LoadFileForm loadFileForm;
	private BufferForm bufferForm;
	private StoreForm storeForm;
	private PluginsForm pluginsForm;
	private SettingsForm settingsForm;
	private VerticalLayout content = new VerticalLayout();
	
	@Override
    protected void init(VaadinRequest vaadinRequest) {
		if (getSession().getAttribute("singed") != null) {
			loginExistSession();
		} else {
			loginNoSession();
		}
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
    
    public void loginExistSession() {
    	// menu components
		MenuForm menu = new MenuForm(this);
		menu.setMargin(false);
		head.setContent(menu);

		// body components
		LogoBackground logoBackgroung = new LogoBackground();
		body.setContent(logoBackgroung);

		// about component
		AboutForm about = new AboutForm();
		about.setMargin(false);
		footer.setContent(about);
		
		// content components
		content.setMargin(true);
		content.setSpacing(true);
		content.addComponents(head, body, footer);
		setContent(content);
	}
	
	public void loginNoSession(){
		LoginForm loginForm = new LoginForm(this);
		setContent(loginForm);
	}

	public void logout() {
		// clear component
		LoginForm loginForm = new LoginForm(this);
		cleanupAfterLogout();
		setContent(loginForm);
	}
	
	private void cleanupAfterLogout(){
		getSession().setAttribute("settingsUrl", null);
		getSession().getSession().invalidate();
		Page.getCurrent().setLocation(VaadinServlet.getCurrent().getServletContext().getContextPath());
		content.removeAllComponents();
	}
    
    public void setContentBodyLocalMaven(){
    	localMavenForm = new LocalMavenForm(this);
    	body.setContent(localMavenForm);
    }
    
    public void setContentBodyCentralMaven(){
    	centralMavenForm = new CentralMavenForm(this);
    	body.setContent(centralMavenForm);
    }
    
    public void setContentBodyCheckMavenIndexForm(){
    	checkMavenIndexFrom = new CheckMavenIndexForm(this);
    	body.setContent(checkMavenIndexFrom);
    }
    
    public void setContentBodyDefinedMaven(){
    	definedMavenForm = new DefinedMavenForm(this);
    	body.setContent(definedMavenForm);
    }
    
    public void setContentBodyLoadFile(){
    	loadFileForm = new LoadFileForm(this.getSession());
    	body.setContent(loadFileForm);
    }
    
    public void setContentBodyBuffer(){
    	bufferForm = new BufferForm(this);
    	body.setContent(bufferForm);
    }
    
    public void setContentBodyStore(){
    	storeForm = new StoreForm();
    	body.setContent(storeForm);
    }
    
    public void setContentBodyPlugins(){
    	pluginsForm = new PluginsForm(this);
    	body.setContent(pluginsForm);
    }
    
    public void setContentSettings(){
    	settingsForm = new SettingsForm(this.getSession());
    	body.setContent(settingsForm);
    }
}
