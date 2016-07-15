package com.honam.base.api.service;


import com.honam.base.api.annotation.APIField;
import com.honam.base.api.annotation.APIMethod;
import com.honam.base.api.annotation.APIParam;
import com.honam.base.api.annotation.APIValueObject;
import com.honam.base.api.vo.*;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created by xuhaonan on 15/11/27.
 */
@Service("ApiScanEngine")
public class ApiScanEngine {

    @Autowired
    private ApplicationContext applicationContext;

    private List<ApiParam> getApiParamList(Method method) {
        //Class<?> returnType = method.getReturnType();
        Class<?>[] paramterTypes = method .getParameterTypes();
        Annotation[][] paramAnnotations = method .getParameterAnnotations();
        ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = parameterNameDiscoverer .getParameterNames(method);
        List<ApiParam> paramVOList = new ArrayList<ApiParam>();
        for (int i = 0; i < paramterTypes.length; i++) {
            ApiParam apiParamVO = new ApiParam();
            apiParamVO.setClassName(paramterTypes[i] .getName());
            apiParamVO.setName(paramNames[i]);
            if (paramterTypes[i].isArray()) {
                apiParamVO.setIsCollection(true);
                apiParamVO.setCollectionClass(paramterTypes[i].getName());
                Class<?> elementType = paramterTypes[i].getComponentType();
                apiParamVO.setClassName(elementType.getName());
                boolean isValueObject = elementType.getAnnotation(APIValueObject.class) == null ? false : true;
                apiParamVO.setIsValueObject(isValueObject);
            } else if (Collection.class.isAssignableFrom(paramterTypes[i])) {
                apiParamVO.setIsCollection(true);
                apiParamVO.setCollectionClass(paramterTypes[i].getName());
                Type elementType = method.getGenericParameterTypes()[i];
                if (elementType != null && elementType instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) elementType;
                    Type acctualType=pType.getActualTypeArguments()[0];
                    if (acctualType !=null && acctualType instanceof Class) {
                        Class<?> genericClazz = (Class<?>) pType.getActualTypeArguments()[0];
                        apiParamVO.setClassName(genericClazz.getName());
                        boolean isValueObject = genericClazz.getAnnotation(APIValueObject.class) == null ? false : true;
                        apiParamVO.setIsValueObject(isValueObject);
                    }else{
                        apiParamVO.setClassName(acctualType.toString());
                        apiParamVO.setIsValueObject(false);
                    }
                }
            } else if(paramterTypes[i].getAnnotation(APIValueObject.class)!=null){
                apiParamVO.setIsValueObject(true);
                apiParamVO.setIsCollection(false);
            } else {
                apiParamVO.setIsValueObject(false);
                apiParamVO.setIsCollection(false);
            }

            for (int j = 0; j < paramAnnotations[i].length; j++) {
                if (0 != paramAnnotations[i].length
                        && null != paramAnnotations[i][j]) {
                    if (paramAnnotations[i][j] .annotationType().equals( APIParam.class)) {
                        APIParam openAPIParam = (APIParam) paramAnnotations[i][j];
                        apiParamVO.setDescription(openAPIParam.description());
                        apiParamVO.setRequired(openAPIParam.isRequired());
                    }
                    if (paramAnnotations[i][j] .annotationType().equals( RequestBody.class)) {
                        apiParamVO.setIsJson(true);
                    }
                }
            }
            paramVOList.add(apiParamVO);
        }
        return paramVOList;
    }

    private ApiReturn getApiReturn(Method method) {
        Class<?> returnType = method.getReturnType();
        ApiReturn apiReturn = new ApiReturn();
        if (returnType.isArray()) {
            apiReturn.setIsCollection(true);
            apiReturn.setCollectionClass(returnType.getName());
            Class<?> elementType = returnType.getComponentType();
            apiReturn.setClassName(elementType.getName());
            boolean isValueObject = elementType.getAnnotation(APIValueObject.class) == null ? false : true;
            apiReturn.setIsValueObject(isValueObject);
        } else if (Collection.class.isAssignableFrom(returnType)) {
            apiReturn.setIsCollection(true);
            apiReturn.setCollectionClass(returnType.getName());
            Type elementType = method.getGenericReturnType();
            if (elementType != null && elementType instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) elementType;
                Type acctualType=pType.getActualTypeArguments()[0];
                if (acctualType !=null && acctualType instanceof Class) {
                    Class<?> genericClazz = (Class<?>) pType.getActualTypeArguments()[0];
                    apiReturn.setClassName(genericClazz.getName());
                    boolean isValueObject = genericClazz.getAnnotation(APIValueObject.class) == null ? false : true;
                    apiReturn.setIsValueObject(isValueObject);
                }else{
                    apiReturn.setClassName(acctualType.toString());
                    apiReturn.setIsValueObject(false);
                }

            }
        } else if (returnType.getAnnotation(APIValueObject.class) != null) {
            apiReturn.setIsValueObject(true);
            apiReturn.setIsCollection(false);
            apiReturn.setClassName(returnType.getName());
        } else {
            apiReturn.setIsValueObject(false);
            apiReturn.setIsCollection(false);
            apiReturn.setClassName(returnType.getName());
        }
        return apiReturn;
    }

    public Map<String, List<ApiMethod>> getAllAPIMethod() {
        final Map<String, List<ApiMethod>> categoryApiMap = new HashMap<String, List<ApiMethod>>();
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(Controller.class);
        for (final String beanName : map.keySet()) {
            final Object controller = map.get(beanName);
            final RequestMapping classRM = controller.getClass().getAnnotation(RequestMapping.class);
            ReflectionUtils.doWithMethods(controller.getClass(), new ReflectionUtils.MethodCallback() {
                public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                    APIMethod api = method .getAnnotation(APIMethod.class);
                    RequestMapping methodRM = method.getAnnotation(RequestMapping.class);
                    String url = classRM.value()[0] + "/" + methodRM.value()[0] + ".do";//only support first url mapping
                    String category = api.category().toString();
                    if (null == categoryApiMap.get(category)) {
                        categoryApiMap.put(category, new ArrayList<ApiMethod>());
                    }

                    ApiMethod apiVO = new ApiMethod();
                    apiVO.setName(controller.getClass().getSimpleName() + "." + method.getName());//method name
                    apiVO.setCategory(category);
                    apiVO.setUrl(new String[]{url});
                    apiVO.setDescription(api.desc());
                    apiVO.setSupportHttp(method.getAnnotation(RequestMapping.class) != null);

                    List<String> apiAuthors = new ArrayList<String>();
                    for(ApiAuthor apiAuthor : api.apiAuthors()) {
                        apiAuthors.add(apiAuthor.toString());
                    }
                    apiVO.setApiAuthors(apiAuthors);

                    ApiReturn apiReturn = getApiReturn(method);
                    apiVO.setApiReturn(apiReturn);

                    List<ApiParam> paramVOList = getApiParamList(method);
                    apiVO.setParam(paramVOList);

                    categoryApiMap.get(category).add(apiVO);
                }
            },new ReflectionUtils.MethodFilter() {
                public boolean matches(Method method) {
                    if (AnnotationUtils.getAnnotation(method, APIMethod.class) != null) {
                        return true;
                    }
                    return false;
                }
            });
        }
        return categoryApiMap;
    }

    public List<ApiParam> getMethodParam(String beanName,
                                         final String methodName) {
        Object bean = applicationContext.getBean(beanName);
        if (null == bean) {
            throw new NoSuchBeanDefinitionException(beanName);
        }
        final List<ApiParam> paramVOList = new ArrayList<ApiParam>();
        final List<Method> foundMethodList = new ArrayList<Method>();
        ReflectionUtils.doWithMethods(bean.getClass(),
                new ReflectionUtils.MethodCallback() {
                    public void doWith(Method method)
                            throws IllegalArgumentException {

                        foundMethodList.add(method);
                        ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
                        String[] paramNames = parameterNameDiscoverer
                                .getParameterNames(method);
                        Class<?>[] paramterTypes = method.getParameterTypes();
                        Class<?>[] params = method.getParameterTypes();
                        Annotation[][] paramAnnotations = method
                                .getParameterAnnotations();
                        for (int i = 0; i < params.length; i++) {
                            ApiParam apiParamVO = new ApiParam();
                            apiParamVO.setClassName(paramterTypes[i].getName());
                            apiParamVO.setName(paramNames[i]);

                            for (int j = 0; j < paramAnnotations[i].length; j++) {
                                if (0 != paramAnnotations[i].length
                                        && null != paramAnnotations[i][j]) {
                                    if (paramAnnotations[i][j].annotationType()
                                            .equals(APIParam.class)) {
                                        APIParam openAPIParam = (APIParam) paramAnnotations[i][j];
                                        apiParamVO.setDescription(openAPIParam
                                                .description());
                                    }
                                    if (paramAnnotations[i][j].annotationType()
                                            .equals(RequestBody.class)) {
                                        apiParamVO.setIsJson(true);
                                    }

                                }
                            }
                            paramVOList.add(apiParamVO);
                        }

                    }
                }, new ReflectionUtils.MethodFilter() {

                    public boolean matches(Method method) {

                        if (ClassUtils.isCglibProxyClassName(method
                                .getDeclaringClass().getName())) {
                            return false;

                        }
                        if (Proxy.isProxyClass(method.getDeclaringClass()
                                .getClass())) {
                            return false;

                        }
                        if (method.getName().equalsIgnoreCase(methodName)) {
                            return true;

                        }

                        return false;
                    }

                });

        if (foundMethodList.isEmpty()) {
            throw new NoSuchMethodError(beanName.getClass().toString()
                    + methodName);
        }

        return paramVOList;
    }


    public ApiValueObject getValueObjectDesc(String className) throws ClassNotFoundException {
        Class<?> objectValueClazz = Class.forName(className);
        APIValueObject valueObjectAnnotation = objectValueClazz.getAnnotation(APIValueObject.class);
        ApiValueObject apiValueObject = new ApiValueObject();
        apiValueObject.setClassName(className);
        apiValueObject.setDescription(valueObjectAnnotation.value());
        List<ApiField> apiFieldVOList = apiValueObject.getApiFields();
        Field[] fields = objectValueClazz.getDeclaredFields();
        for (Field field : fields) {
            APIField fieldDescription = field.getAnnotation(APIField.class);
            if (null != fieldDescription) {
                ApiField apiFieldVO = new ApiField();
                apiFieldVO.setName(field.getName());
                apiFieldVO.setDescription(fieldDescription.value());

                if (field.getType().isArray()) {
                    apiFieldVO.setIsCollection(true);
                    apiFieldVO.setCollectionClass(field.getType().getName());
                    Class<?> elementType = field.getType().getComponentType();
                    apiFieldVO.setClassName(elementType.getName());
                    boolean isValueObject=elementType.getAnnotation(APIValueObject.class)==null?false:true;
                    apiFieldVO.setIsValueObject(isValueObject);

                }else if (Collection.class.isAssignableFrom(field.getType())) {
                    apiFieldVO.setIsCollection(true);
                    apiFieldVO.setCollectionClass(field.getType().getName());
                    Type elementType=field.getGenericType();

                    if (elementType!=null) {
                        if(elementType instanceof ParameterizedType)
                        {
                            ParameterizedType pType = (ParameterizedType) elementType;
                            Class<?> genericClazz = (Class<?>) pType.getActualTypeArguments()[0];
                            apiFieldVO.setClassName(genericClazz.getName());
                            boolean isValueObject=genericClazz.getAnnotation(APIValueObject.class)==null?false:true;
                            apiFieldVO.setIsValueObject(isValueObject);


                        }
                    }


                }else if(field.getType().getAnnotation(APIValueObject.class)!=null){
                    apiFieldVO.setIsValueObject(true);
                    apiFieldVO.setIsCollection(false);
                    apiFieldVO.setClassName(field.getType().getName());
                }else {
                    apiFieldVO.setIsValueObject(false);
                    apiFieldVO.setIsCollection(false);
                    apiFieldVO.setClassName(field.getType().getName());

                }
                apiFieldVOList.add(apiFieldVO);
            }
        }
        return   apiValueObject;
    }
}
