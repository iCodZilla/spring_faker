package ioc;

import bean.Person;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SimpleIOC {
    // 生成的bean 的容器
    private Map<String, Object> beans = new HashMap<>();

    public SimpleIOC(String location) throws Exception {
        loadBeans(location);
    }

    // 扫描XML配置文件, 反射bean, 并注册到Beans 注册池中.
    private void loadBeans(String location) throws Exception {
        // 读取配置文件
        FileInputStream inputStream = new FileInputStream(location);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(inputStream);
        Element rootElement = doc.getDocumentElement();
        NodeList childNodes = rootElement.getChildNodes();

        // 遍历并反射生成Bean
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element) {
                Element ele = (Element) node;
                String id = ele.getAttribute("id");
                String className = ele.getAttribute("class");

                // 通过bean 参数得到class object
                Class beanClass = null;
                try {
                    beanClass = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                // 通过beanClass 创建bean
//                Object bean = null;
//                try {
//                    bean = beanClass.newInstance();
//                } catch (InstantiationException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }

                Person bean = (Person) beanClass.newInstance();


                // 得到property 标签, 并遍历每个子节点, 得到每个属性的name 和 value
                NodeList propertyNodes = ele.getElementsByTagName("property");

                for (int j = 0; j < propertyNodes.getLength(); j++) {
                    // 得到 name 和 value
                    Node propertyNode = propertyNodes.item(j);
                    // 将Node 转换为 Element 进行操作
                    if (propertyNode instanceof Element) {
                        Element property = (Element) propertyNode;
                        String propertyName = property.getAttribute("name");
                        String propertyValue = property.getAttribute("value");

                        System.out.println("属性名称: " + propertyName);
                        System.out.println("属性值: " + propertyValue);
                        System.out.println("-----");

                        // 将该字段设定为可访问, 以便于设定属性值
                        Field field = bean.getClass().getDeclaredField(propertyName);

//                        field.setAccessible(true);// 设置为可访问, 以便以设置保护属性为非public 的对象属性

                        // 这里为何要一个bean 参数? 如果不是静态属性, 那么需要一个对象来承接
                        // 如果属性为final, 抛出异常, 具体看定义
                        Method nameSetter = bean.getClass().getMethod("setName", String.class);
                        nameSetter.invoke(bean, propertyValue);
                    }
                }

                addBean(id, bean);
            }
        }
    }

    // 注册 Bean
    private void addBean(String name, Object object) {
        beans.put(name, object);
    }

    // 提取 Bean
    public Object getBean(String name) {
        Object bean = beans.get(name);
        if (bean == null) {
            throw new RuntimeException("Bean 注册池中未发现ID为: " + name + " 的bean");
        }

        return bean;
    }
}
