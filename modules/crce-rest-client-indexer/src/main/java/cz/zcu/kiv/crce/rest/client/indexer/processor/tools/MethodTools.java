package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.config.ArgConfigType;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.ArgConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.MethodArgType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.EndpointData;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.VarArray;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable.VariableType;

public class MethodTools {



    public enum MethodType {
        OTHER, INIT
    }

    private static final String initString = "<init>";
    private static final Pattern argPattern = Pattern.compile("\\((.*?)\\)");
    private static final Pattern methodNamePattern = Pattern.compile("\\.<?(\\w*)>?-?\\(");

    /**
     * Checkes if
     * 
     * @param signature Signature of the method
     * @return
     */
    public static MethodType getType(String signature) {
        if (signature == null) {
            return null;
        }
        if (signature.contains(initString)) {
            return MethodType.INIT;
        }
        return MethodType.OTHER;
    }

    /**
     * Retrieves arguments from signature of method
     * 
     * @param signature Signature of method
     * @return Parsed arguments
     */
    public static String[] getArgsFromSignature(String signature) {
        Matcher matcher = argPattern.matcher(signature);
        if (matcher.find()) {
            String[] args = matcher.group(1).split(";");
            if (args == null || args.length == 0 || args[0].equals("")) {
                return null;
            }
            // cleanup
            int counter = 0;
            for (String arg : args) {
                //System.out.println("ARG=" + arg);
                args[counter++] = ClassTools.descriptionToClassPath(arg);
            }
            return args;
        }
        return null;
    }

    /**
     * Retrieves method name from the signature
     * 
     * @param signature Signature of method
     * @return Name of the method
     */
    public static String getMethodNameFromSignature(String signature) {
        Matcher matcher = methodNamePattern.matcher(signature);
        if (matcher.find()) {
            final String found = matcher.group(1);
            return found;
        }
        return null;
    }





    /**
     * Retrieves parameters from stack based on definition of method arguments
     * 
     * @param values Stack
     * @param args Definition of arguments definition
     * @return Endpoint parameters
     
    public static Map<String, Object> getParams(Stack<Variable> values,
            Set<ArrayList<ArgConfigType>> args) {
        Map<String, Object> output = new HashMap<>();
        if (args == null || values.isEmpty()) {
            return output;
        }
        for (final ArrayList<ArgConfigType> versionOfArgs : args) {
            if (versionOfArgs.size() == values.size()) {
                for (ArgConfigType definition : versionOfArgs) {
                    final Variable var = values.pop();
                    final Object val = var.getValue();
                    if (definition == ArgConfigType.SKIP) {
                        continue;
                    }
                    if (output.containsKey(definition.name())) {
                        output.put(definition.name(),
                                output.get(definition.name()) + getStringValueVar(var));
                    } else if (val instanceof VarArray) {
                        VarArray arrayCasted = (VarArray) val;
                        output.put(definition.name(), arrayCasted.getInnerArray());
                    } else if (val instanceof VarEndpointData) {
                        output.put(definition.name(), val);
                    } else if (val instanceof Endpoint) {
                        output.put(definition.name(), val);
                    } else {
                        output.put(definition.name(), getStringValueVar(var));
                    }
    
                }
            }
        }
        return output;
    }
    */
}
