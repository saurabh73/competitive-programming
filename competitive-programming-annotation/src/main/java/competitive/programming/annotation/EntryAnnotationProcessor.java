package competitive.programming.annotation;

import lombok.Getter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ReferenceType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("competitive.programming.annotation.leetcode.Entry")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class EntryAnnotationProcessor extends AbstractProcessor {

    @Getter
    private final List<MethodSignature> methodSignatures;
    @Getter
    private final Set<String> importClasses;

    public EntryAnnotationProcessor(){
        methodSignatures = new ArrayList<>();
        importClasses = new TreeSet<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            if (annotatedElements.size() == 1) {
                Element element = (Element) annotatedElements.toArray()[0];
                if (element instanceof ExecutableElement) {
                    MethodSignature signature = new MethodSignature();
                    ExecutableElement method = (ExecutableElement) element;

                    // Set Name
                    String name = element.getSimpleName().toString();
                    signature.setName(name);

                    //Set Return Type
                    TypeMirror returnType = method.getReturnType();
                    signature.setReturnType(typeName(returnType));

                    // Set access modifier
                    List<String> modifierList = element.getModifiers().stream().map(Modifier::toString).collect(Collectors.toList());
                    signature.setAccessModifiers(new ArrayList<>(modifierList));
                    if (!modifierList.contains("public")) {
                        throw new AnnotationProcessorException("@Entry should be annotated only on public methods");
                    }

                    // Set Parameters
                    List<? extends VariableElement> parameters = method.getParameters();
                    LinkedHashMap<String, String> parameterMap = new LinkedHashMap<>();
                    for(VariableElement parameter: parameters) {
                        parameterMap.put(parameter.getSimpleName().toString(), typeName(parameter.asType()));
                    }
                    signature.setParameters(parameterMap);
                    methodSignatures.add(signature);
                } else {
                    throw new AnnotationProcessorException("@Entry should be annotated only on methods");
                }
                
            } else if (annotatedElements.size() > 1) {
                throw new AnnotationProcessorException("Only single of @Entry is allowed");
            }
        }
        return false;
    }

    private String typeName(TypeMirror type) {
        if (type instanceof ReferenceType) {
            if (type instanceof ArrayType) {
                TypeMirror arrayType = ((ArrayType) type).getComponentType();
                return typeName(arrayType)+ "[]";
            } else {
                DeclaredType declaredType = (DeclaredType) type;
                TypeElement declaredElement = ((TypeElement )declaredType.asElement());
                importClasses.add(declaredElement.getQualifiedName().toString());
                if (declaredType.getTypeArguments().isEmpty()) {
                    return declaredElement.getSimpleName().toString();
                } else {
                    StringBuilder genericsBuilder = new StringBuilder();
                    for(TypeMirror genericType : declaredType.getTypeArguments()) {
                        genericsBuilder.append(typeName(genericType)).append(",");
                    }
                    genericsBuilder.setLength(genericsBuilder.length() - 1);
                    return String.format("%s<%s>", declaredElement.getSimpleName().toString(), genericsBuilder.toString());
                }
            }
        } else {
            return type.getKind().name().toLowerCase();
        }
    }
}
