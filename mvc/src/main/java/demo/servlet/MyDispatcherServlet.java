package demo.servlet;

import com.sun.xml.internal.ws.util.StringUtils;
import demo.annotaion.MyAutowired;
import demo.annotaion.MyController;
import demo.annotaion.MyRequestMapping;
import demo.annotaion.MyService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * create by hanshin on 2021/4/28
 */
public class MyDispatcherServlet extends HttpServlet {

    List<String> classNameList = new ArrayList<>();
    Map<String, Object> iocMap = new HashMap<>();
    Map<String, Method> handlerMapping = new HashMap<>();
    Map<String, Object> controllerMapping = new HashMap<>();

    //web.xml配置dispatcherServlet，启动时执行init
    @Override
    public void init() throws ServletException {
        System.out.println("---MyDispatcherServlet start----");
        //扫注解标注的包
        doScanPackage("demo");
        //实例化注解标注的类
        doInstance();
        //依赖注入
        doAutowired();
        //映射
        doMapping();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatcher(req, resp);
    }

    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(handlerMapping.isEmpty()){
            return;
        }
        String url = req.getRequestURI().replace(req.getContextPath(), "").replaceAll("/+", "/");
        if(!handlerMapping.containsKey(url)){
            resp.getWriter().write("404");
            return ;
        }

        Method method = handlerMapping.get(url);
        //获取方法的参数列表
        Class<?>[] parameterTypes = method.getParameterTypes();

        //获取请求的参数
        Map<String, String[]> parameterMap = req.getParameterMap();

        //保存参数值
        Object [] paramValues= new Object[parameterTypes.length];
        for (int i = 0; i<parameterTypes.length; i++){
            //根据参数名称，做某些处理
            String requestParam = parameterTypes[i].getSimpleName();


            if (requestParam.equals("HttpServletRequest")){
                //参数类型已明确，这边强转类型
                paramValues[i]=req;
                continue;
            }
            if (requestParam.equals("HttpServletResponse")){
                paramValues[i]=resp;
                continue;
            }
            if(requestParam.equals("String")){
                for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
                    String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
                    paramValues[i]=value;
                }
            }
        }

        try {
            //利用反射机制来调用
            Object r = method.invoke(controllerMapping.get(url), paramValues);//第一个参数是method所对应的实例 在ioc容器中
            resp.getWriter().println(r.toString());
//            String view = "/" + r.toString() + ".jsp";
//            req.getRequestDispatcher(view).forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doMapping() {
        if(iocMap.isEmpty()){
            return;
        }
        try {
            for(Map.Entry<String, Object> entry : iocMap.entrySet()){
                Class<?> clazz = entry.getValue().getClass();
                if(!clazz.isAnnotationPresent(MyController.class)){
                    continue;
                }

                String baseUrl = "";
                if(clazz.isAnnotationPresent(MyRequestMapping.class)){
                    MyRequestMapping myRequestMapping = clazz.getAnnotation(MyRequestMapping.class);
                    baseUrl = myRequestMapping.value();
                }

                Method[] methods = clazz.getMethods();
                for(Method method : methods){
                    if(method.isAnnotationPresent(MyRequestMapping.class)){
                        MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);
                        String url = (baseUrl + requestMapping.value()).replaceAll("/+", "/");
                        handlerMapping.put(url, method);
                        controllerMapping.put(url, entry.getValue()); //单例，映射的是ioc容器中存在的bean
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void doAutowired() {
        if(classNameList.isEmpty()){
            return;
        }

        for(Map.Entry<String, Object> entry : iocMap.entrySet()){
            try {
                Field[] fields = entry.getValue().getClass().getDeclaredFields();
                for(Field field : fields){
                    if(!field.isAnnotationPresent(MyAutowired.class)){
                        continue;
                    }
                    MyAutowired myAutowired = field.getAnnotation(MyAutowired.class);
                    String beanName = "".equals(myAutowired.value()) ?
                            toLowerFirstWord(field.getType().getSimpleName()) : myAutowired.value();
                    Object bean = iocMap.get(beanName);
                    if(bean == null){
                        System.out.println(beanName + "not found");

                    }

                    field.setAccessible(true);
                    field.set(entry.getValue(), bean);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void doInstance() {
        if(classNameList.isEmpty()){
            return;
        }

        for(String clz : classNameList){
            try {
                Class<?> clazz = Class.forName(clz);
                if(clazz.isAnnotationPresent(MyController.class)){
                    iocMap.put(toLowerFirstWord(clazz.getSimpleName()), clazz.newInstance());
                }

                if(clazz.isAnnotationPresent(MyService.class)){
                    MyService myService = clazz.getAnnotation(MyService.class);
                    String beanName = "".equals(myService.value()) ?
                            toLowerFirstWord(clazz.getSimpleName()) : myService.value();
                    Object instance = clazz.newInstance();
                    iocMap.put(beanName, instance);

                    Class<?>[] interfaces = clazz.getInterfaces();
                    for(Class<?> i : interfaces){
                        //service实现类的接口的bean设置为相同的bean
                        iocMap.put(toLowerFirstWord(i.getSimpleName()), instance);
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String toLowerFirstWord(String simpleName) {
        char[] arr = simpleName.toCharArray();
        arr[0] += 32;
        return String.valueOf(arr);
    }

    private void doScanPackage(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for(File file : dir.listFiles()){
            if(file.isDirectory()){
                doScanPackage(packageName + "." + file.getName());
            }else{
                String className = packageName + "." + file.getName().replace(".class", "");
                classNameList.add(className);
            }

        }
    }
}
