package net.mostow.util.exchanger;

import com.google.gson.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class Exchanger {
    protected final String xmlNamelessNode = "L";
	@SuppressWarnings("unchecked")
	public <T> T swapToObject(String inputDTO, Class<T> cls) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		JsonElement json = JsonParser.parseString(inputDTO);
		if(json.isJsonObject()) {
			return swapToObject(json.getAsJsonObject(), cls);
		} else if(json.isJsonArray()) {
			return null;// TODO: be build
		} else if(json.isJsonNull()) {
			return null;
		} else
			return (T) json.getAsString();
	}
	public <T> T swapToObject(JsonObject json, Class<T> cls) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		T outputObject = cls.getDeclaredConstructor().newInstance();
		JsonArray parseErrors = new JsonArray();
		for(Field field : cls.getDeclaredFields()) {
			String defaultStringValue = "";
			Date parsedDate = null;
			Long parsedNumber = null;
			JsonMandatory mandatoryAnnotation = field.getAnnotation(JsonMandatory.class);
			String fieldName = field.getName();
			if (mandatoryAnnotation != null) {
				if (!json.has(fieldName) || nvl(json.get(fieldName), mandatoryAnnotation.defaultValue()).equals(""))
					parseErrors.add(new JsonPrimitive(failureHandler(field, JsonMandatory.class, JsonMandatory.failureMessage)));
				if (nvl(json.get(fieldName), mandatoryAnnotation.defaultValue()).length() > mandatoryAnnotation.maximumLength())
					parseErrors.add(new JsonPrimitive(failureHandler(field, JsonMandatory.class, JsonMandatory.maximumLengthError)));
				if (nvl(json.get(fieldName), mandatoryAnnotation.defaultValue()).length() < mandatoryAnnotation.minimumLength())
					parseErrors.add(new JsonPrimitive(failureHandler(field, JsonMandatory.class, JsonMandatory.minimumLengthError)));
			}
			JsonNumber numberAnnotation = field.getAnnotation(JsonNumber.class);
			if(numberAnnotation != null || Number.class.isAssignableFrom(field.getType()) || int.class.isAssignableFrom(field.getType()) || long.class.isAssignableFrom(field.getType())) {
				if (numberAnnotation!=null)
					defaultStringValue = numberAnnotation.defaultValue();
				try {
					parsedNumber = Long.parseLong(nvl(json.get(fieldName), defaultStringValue));
				} catch (NumberFormatException e) {
					parseErrors.add(new JsonPrimitive(failureHandler(field, JsonNumber.class, JsonNumber.failureMessage)));
					continue;
				}
			}
			JsonDate dateAnnotation = field.getAnnotation(JsonDate.class);
			if((dateAnnotation != null || field.getType().equals(Date.class)) && !(json.get(fieldName) == null || json.get(fieldName).isJsonNull())){
                try {
					String formatInString = dateAnnotation==null ? JsonDate.defaultFormat : dateAnnotation.dateFormat();
					parsedDate = new SimpleDateFormat(formatInString).parse(nvl(json.get(fieldName), ""));
				} catch (ParseException e) {
					parseErrors.add(new JsonPrimitive(failureHandler(field, JsonDate.class, JsonDate.failureMessage)));
					continue;
                }
            }
			try {
				field.setAccessible(true);
				if (field.getType().isArray()) {
					field.set(outputObject, new Gson().fromJson(json.get(fieldName).getAsJsonArray(), field.getType()));
				} else if(String.class.equals(field.getType())) {
					field.set(outputObject, nvl(json.get(fieldName), defaultStringValue));
				} else if(Date.class.equals(field.getType())) {
                    field.set(outputObject, parsedDate);
                } else if(boolean.class.equals(field.getType()) || Boolean.class.equals(field.getType())) {
                    String jsonValue = nvl(json.get(fieldName), defaultStringValue);
                    field.set(outputObject, jsonValue.equals("1") || jsonValue.equalsIgnoreCase("true"));
                } else if(long.class.equals(field.getType()) || Long.class.equals(field.getType())) {
					if (parsedNumber!=null)
						field.set(outputObject, parsedNumber);
				} else if(int.class.isAssignableFrom(field.getType()) || Integer.class.isAssignableFrom(field.getType())) {
					if (parsedNumber!=null)
						field.set(outputObject, parsedNumber.intValue());
				} else if(JsonElement.class.isAssignableFrom(field.getType())) {
					if(json.get(fieldName) == null || json.get(fieldName).isJsonNull()) {
						continue;
					}
					field.set(outputObject, JsonParser.parseString(json.get(fieldName).toString()));
				} else {
					field.set(outputObject, new Gson().fromJson(json.get(fieldName), field.getType()));
				}
			} catch(IllegalArgumentException e) {
				parseErrors.add(new JsonPrimitive(e.getMessage()));
			} catch(IllegalAccessException e) {
				parseErrors.add(new JsonPrimitive(e.getMessage()));
			}
		}
		if (parseErrors.size() > 0)
			throw new IllegalAccessException(parseErrors.toString());
		return outputObject;
	}

	public <T> JsonElement swapToJson(T inputObject) throws IllegalArgumentException, IllegalAccessException{
		if(inputObject==null)
			return new JsonObject();
		return swapToJson(inputObject, inputObject.getClass(), null);
	}
	private <T> JsonElement swapToJson(Object inputObject, Class<T> cls, Type type) throws IllegalArgumentException, IllegalAccessException{
		JsonObject outputObject = new JsonObject();
		if(List.class.isAssignableFrom(cls)) {
			JsonArray outputAsArray = new JsonArray();
			Class<?> subArrayClass;
			for(Object subListObj : (List<?>) inputObject) {
				if(type != null) {
					ParameterizedType pType = (ParameterizedType) type;
					subArrayClass =  (Class<?>) pType.getActualTypeArguments()[0];
				} else {
					subArrayClass = subListObj.getClass();
				}
				outputAsArray.add(swapToJson(subListObj, subArrayClass, null));
			}
			return outputAsArray;
		} else if(JsonElement.class.isAssignableFrom(cls)){
			return (JsonElement) inputObject;
		} else if(Number.class.isAssignableFrom(cls) || int.class.isAssignableFrom(cls) || long.class.isAssignableFrom(cls)){
			return new JsonPrimitive((Number) inputObject);
		} else if(Boolean.class.isAssignableFrom(cls)){
			return new JsonPrimitive((Boolean) inputObject);
		} else if(String.class.isAssignableFrom(cls)){
			return new JsonPrimitive(inputObject.toString());
		}
		for(Field field : cls.getDeclaredFields()) {
			if(field.getAnnotation(JsonTransient.class) != null)
				continue;
			field.setAccessible(true);
			Class<?> fieldType = field.getType();
			String fieldName = field.getName();
			Object fieldValue = field.get(inputObject);
			if(fieldValue == null) {
				outputObject.add(fieldName, JsonNull.INSTANCE);
			} else {
				if(JsonElement.class.isAssignableFrom(fieldType)) {
					outputObject.add(fieldName, (JsonElement) fieldValue);
				}else if(List.class.isAssignableFrom(fieldType)) {
					outputObject.add(fieldName, swapToJson(fieldValue, fieldType, field.getGenericType()));
				}else if(Number.class.isAssignableFrom(fieldType) || int.class.isAssignableFrom(fieldType) || long.class.isAssignableFrom(fieldType)){
					outputObject.addProperty(fieldName, (Number) fieldValue);
				}else if(Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType)){
					outputObject.addProperty(fieldName, (Boolean) fieldValue);
				}else
					outputObject.add(fieldName, swapToJson(fieldValue, fieldType, null));
			}
		}
		return outputObject;
	}
	public String jsonToXml(String inputDTO) {
		return jsonToXml(inputDTO, 0).toString();
	}
	private StringBuilder jsonToXml(String inputDTO, int layer) {
		return jsonToXml(inputDTO, xmlNamelessNode, layer);
	}
	private StringBuilder jsonToXml(String inputDTO, String parentNode, int layer) {
		JsonElement jsonEntity = JsonParser.parseString(inputDTO);
		StringBuilder xmlStream = new StringBuilder();
		if(jsonEntity.isJsonArray()) {
			for (int i = 0; i < jsonEntity.getAsJsonArray().size(); i++) {
				xmlStream
						.append("<").append(parentNode)/*.append(":").append(i)*/.append(">")
							.append(jsonToXml(jsonEntity.getAsJsonArray().get(i).toString(), layer + 1))
						.append("</").append(parentNode)/*.append(":").append(i)*/.append(">");
			}
		} else {
			for(Map.Entry<String, JsonElement> entry : jsonEntity.getAsJsonObject().entrySet()) {
				String key = entry.getKey();
				JsonElement value = jsonEntity.getAsJsonObject().get(entry.getKey());
				if(value.isJsonNull())
					xmlStream.append("<").append(key).append("></").append(key).append(">");
				else if (value.isJsonPrimitive())
					xmlStream.append("<").append(key).append(">").append(value.getAsString()).append("</").append(key).append(">");
				else
					xmlStream.append("<").append(key).append(">").append(jsonToXml(value.toString(), key, layer + 1)).append("</").append(key).append(">");
			}
		}
		return xmlStream;
	}

	private String nvl(JsonElement jsonElement, String defaultText) {
		if(jsonElement==null || jsonElement.isJsonNull())
			return defaultText;
		if(jsonElement.isJsonPrimitive())
			if("-1".equals(jsonElement.getAsString()) || "".equals(jsonElement.getAsString()) || "null".equals(jsonElement.getAsString()))
				return defaultText;
		if(jsonElement.isJsonArray()) {
			if(jsonElement.getAsJsonArray().size()==0)
				return defaultText;
			return jsonElement.getAsJsonArray().toString();
		}
		if(jsonElement.isJsonPrimitive())
			return jsonElement.getAsString();
		return jsonElement.toString();
	}
//	private void failureHandler(Field field, Class<?> annotationClass) throws IllegalAccessException{
//		String annotationFailureMessage = null;
//		try {
//			annotationFailureMessage = annotationClass.getDeclaredField(defaultMessageField).get(null).toString();
//		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | NullPointerException e) {}
//		failureHandler(field, annotationClass, annotationFailureMessage);
//	}
	private String failureHandler(Field field, Class<?> annotationClass, String failureMessage){
		String fieldName = (field.getAnnotation(JsonDescription.class)==null || field.getAnnotation(JsonDescription.class).description().equals("")) ? field.getName() : field.getAnnotation(JsonDescription.class).description();
		String annotationFailureMessage = (failureMessage != null ? failureMessage : annotationClass.getName());
		return annotationFailureMessage + "(" + fieldName + ")";
	}
}
