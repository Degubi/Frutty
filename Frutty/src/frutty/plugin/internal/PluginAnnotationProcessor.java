package frutty.plugin.internal;

import static javax.lang.model.element.Modifier.*;
import static javax.tools.Diagnostic.Kind.*;

import frutty.plugin.*;
import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.element.*;

public class PluginAnnotationProcessor extends AbstractProcessor {

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Set.of("frutty.plugin.FruttyPlugin", "frutty.plugin.FruttyPluginMain", "frutty.plugin.FruttyEventHandler", "frutty.plugin.internal.FruttyEvent");
	}
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
		var messager = processingEnv.getMessager();
		var elementUtils = processingEnv.getElementUtils();

		for(var element : env.getElementsAnnotatedWith(FruttyPlugin.class)) {
			var pluginMainMethods = element.getEnclosedElements().stream()
					   					   .filter(parent -> parent.getAnnotation(FruttyPluginMain.class) != null)
					   					   .toArray(Element[]::new);
			
			if(pluginMainMethods.length == 0) {
				messager.printMessage(ERROR, "Class has no method annotated with @FruttyPluginMain", element);
			}else if(pluginMainMethods.length > 1) {
				messager.printMessage(ERROR, "Class has multiple methods annotated with @FruttyPluginMain", element);
			}else{
				var modifiers = pluginMainMethods[0].getModifiers();
				
				if(!modifiers.contains(PUBLIC) || !modifiers.contains(STATIC)) {
					messager.printMessage(ERROR, "Annotated @FruttyPluginMain method must be public and static", pluginMainMethods[0]);
				}
				
				if(!((ExecutableElement)element).getParameters().isEmpty()) {
					messager.printMessage(ERROR, "Annotated @FruttyPluginMain method must not have any parameters", pluginMainMethods[0]);
				}
			}
		}
		
		for(var element : env.getElementsAnnotatedWith(FruttyEventHandler.class)) {
			var modifiers = element.getModifiers();
			
			if(!modifiers.contains(PUBLIC) || !modifiers.contains(STATIC)) {
				messager.printMessage(ERROR, "Annotated @FruttyEventHandler method must be public and static", element);
			}else {
				var parameters = ((ExecutableElement)element).getParameters();
				var paramSize = parameters.size();
				
				if(paramSize == 0) {
					messager.printMessage(ERROR, "Event handler method has no event parameter", element);
				}else if(paramSize > 1) {
					messager.printMessage(ERROR, "Event handler method has more than one parameters", element);
				}else {
					var firstParam = parameters.get(0);
					
					if(elementUtils.getTypeElement(firstParam.asType().toString()).getAnnotation(FruttyEvent.class) == null) {
						messager.printMessage(ERROR, "Parameter is not an event type", firstParam);
					}
				}
			}
		}
		return true;
	}
}