package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HeaderType;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethodExt;

public class WSClientConfig {
    @JsonProperty("classes")
    private Set<String> classNames;

    @JsonProperty("makeRequest")
    private Map<HttpMethodExt, Set<WSClientMethodConfig>> makeRequest;


    /**
     * @return the classNames
     */
    public Set<String> getClassNames() {
        return classNames;
    }

    /**
     * @param classNames the classNames to set
     */
    public void setClassNames(Set<String> classNames) {
        this.classNames = new HashSet<>();
        for (String className : classNames) {
            final String processed = ClassTools.processClassName(className);
            this.classNames.add(processed);
        }
    }

    /**
     * @return the makeRequest
     */
    public Map<HttpMethodExt, Set<WSClientMethodConfig>> getMakeRequest() {
        return makeRequest;
    }

    /**
     * @param makeRequest the makeRequest to set
     */
    public void setMakeRequest(Map<HttpMethodExt, Set<WSClientMethodConfig>> makeRequest) {
        this.makeRequest = makeRequest;
    }

    @JsonProperty("headers")
    private Map<HeaderType, Set<WSClientMethodConfig>> headers;

    /**
     * @return the headers
     */
    public Map<HeaderType, Set<WSClientMethodConfig>> getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(Map<HeaderType, Set<WSClientMethodConfig>> headers) {
        this.headers = headers;
    }

}
