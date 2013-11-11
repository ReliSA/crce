package cz.zcu.kiv.crce.rest.internal.rest.xml;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.rest.GetMetadata;
import cz.zcu.kiv.crce.rest.internal.rest.convertor.ConvertorToBeans;
import cz.zcu.kiv.crce.rest.internal.rest.convertor.IncludeMetadata;
import cz.zcu.kiv.crce.rest.internal.rest.generated.Trepository;

/**
 * Server will provide a metadata information about resources in the repository.
 * @author Jan Reznicek
 *
 */
@Path("/metadata")
public class MetadataResource extends ResourceParent implements GetMetadata{
	
	private static final Logger log = LoggerFactory.getLogger(MetadataResource.class);
    
	
	/**
	  * {@inheritDoc}
	  */
    @GET
    @Produces({MediaType.APPLICATION_XML })
    public Response getMetadata(@QueryParam("filter") String filter, @QueryParam("core") String core, @QueryParam("cap") String cap, @QueryParam("req") String req, @QueryParam("prop") String prop, @Context UriInfo ui) {
    	newRequest();
    	log.debug("Request ({}) - Get metadata request was received.", getRequestId());
    	
    	Resource[] storeResources;
    	
    	IncludeMetadata include = new IncludeMetadata();
    	
    	if(core == null && cap == null && req == null && prop == null) {
    		//include all
    		include.includeAll();
    	} else {
    		if(core != null) {
    			include.setIncludeCore(true);
    		}
    		if(cap != null) {
    			include.setIncludeCaps(true);
    			if(cap.length() > 0) {
    				include.setIncludeCapByName(cap);
    			}
    		}
    		if(req != null) {
    			include.setIncludeReqs(true);
    			if(req.length() > 0) {
    				include.setIncludeReqByName(req);
    			}
    		}
    		if(prop != null) {
    			include.setIncludeProps(true);
    			if(prop.length() > 0) {
    				include.setIncludePropByName(prop);
    			}
    		}
    	}    	
    	
    	
    	try {
        	if(filter != null) {
                log.debug("Filter used to get metadata: {}.", filter);
        		storeResources = Activator.instance().getStore().getRepository().getResources(filter);
        	} else {
        		storeResources = Activator.instance().getStore().getRepository().getResources();
        	}
    		
			if(storeResources.length > 0) {
				ConvertorToBeans conv = new ConvertorToBeans();
				Trepository repository = conv.convertRepository(storeResources, include, ui);
				Response response = Response.ok(createXML(repository)).build();				
				log.debug("Request ({}) - Response was successfully created.", getRequestId());
				return response;
			} else {				
				log.debug("Request ({}) - No resource was found.", getRequestId());
				return Response.status(404).build();
			}
		} catch (WebApplicationException e) {			
			return e.getResponse();
			
		} catch (InvalidSyntaxException e) {
			log.warn("Request ({}) - Invalid syntax of request LDAP filter.", getRequestId());
			log.debug(e.getMessage(), e);
			return Response.status(400).build();
		}

    }
    

	/**
	 * {@inheritDoc}
	 */
	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_XML })
    public Response getMetadataById(@PathParam("id") String id, @QueryParam("core") String core, @QueryParam("cap") String cap, @QueryParam("req") String req, @QueryParam("prop") String prop, @Context UriInfo ui) {
    	newRequest();
    	log.debug("Request ({}) - Get metadata request for resource with id {} was received.",getRequestId() ,id);
    	
    	IncludeMetadata include = new IncludeMetadata();
    	
    	if(core == null && cap == null && req == null && prop == null) {
    		//include all
    		include.includeAll();
    	} else {
    		if(core != null) {
    			include.setIncludeCore(true);
    		}
    		if(cap != null) {
    			include.setIncludeCaps(true);
    			if(cap.length() > 0) {
    				include.setIncludeCapByName(cap);
    			}
    		}
    		if(req != null) {
    			include.setIncludeReqs(true);
    			if(req.length() > 0) {
    				include.setIncludeReqByName(req);
    			}
    		}
    		if(prop != null) {
    			include.setIncludeProps(true);
    			if(prop.length() > 0) {
    				include.setIncludePropByName(prop);
    			}
    		}
    	}
    	
    	try {
			Resource[] storeResources;
			String filter = "(id="+id+")";
			storeResources = Activator.instance().getStore().getRepository().getResources(filter);
			
	    	try {
				if(storeResources.length > 0) {
					ConvertorToBeans conv = new ConvertorToBeans();
					Trepository repository = conv.convertRepository(storeResources, include, ui);
					Response response = Response.ok(createXML(repository)).build();
					log.debug("Request ({}) - Response was successfully created.", getRequestId());
					return response;
				} else {
					log.debug("Request ({}) - No resource was found.", getRequestId());
					return Response.status(404).build();
				}
			} catch (WebApplicationException e) {
				
				return e.getResponse();
			}
			
		} catch (InvalidSyntaxException e) {
			log.warn("Request ({}) - Invalid syntax of request LDAP filter.", getRequestId());
			log.debug(e.getMessage(), e);
			return Response.status(400).build();
		}    	
        
    }
 

}
