package competitive.programming.annotation;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Data
public class MethodSignature {

    private ArrayList<String> accessModifiers;
    private String name;
    private LinkedHashMap<String, String> parameters;
    private String returnType;
    private ArrayList<String> exceptions;

    public String getMethodCall(String instance) {
        StringBuilder methodCall = new StringBuilder(instance).append(".");
        if (!this.getReturnType().equalsIgnoreCase("void")) {
            methodCall.insert(0, "return ");
        }
        methodCall.append(this.name).append("(");
        Set<String> keys = this.getParameters().keySet();
        if (!keys.isEmpty()) {
            methodCall.append(String.join(",", keys));
        }
        methodCall.append(");");
        return methodCall.toString();
    }

    @Override
    public String toString() {
        String modifiers = String.join(" ", this.getAccessModifiers()).trim();
        StringBuilder parametersCall = new StringBuilder();
        String exceptions ="";
        for(Map.Entry<String, String> parameter: this.getParameters().entrySet()) {
            parametersCall.append(parameter.getValue()).append(" ").append(parameter.getKey()).append(",");
        }
        if (parametersCall.length() > 0) {
            parametersCall.setLength(parametersCall.length() - 1);
        }
        if (this.exceptions != null && !this.exceptions.isEmpty()) {
           exceptions = String.format("throws %s", String.join(",", this.exceptions));
        }
        return String.format("%s %s %s(%s) %s",modifiers, this.getReturnType(), this.getName(), parametersCall, exceptions).trim();
    }
}
