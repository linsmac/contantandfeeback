import entities.Mobile021Entity;
import entities.Mobile02Entity;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;

import javax.imageio.spi.ServiceRegistry;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateSessionFactory {
    private static final Logger logger = Logger.getLogger(HibernateSessionFactory.class.getName());
    private static final SessionFactory ourSessionFactory;
    private static final ServiceRegistry serviceRegistry;

    static {
        logger.setLevel(Level.ALL);
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            //一定要记得在这里把生成的实体类加入到Configuration里。否则调用的时候肯定报错。
            //Hibernate 4.x版本的不需要。5.x才需要这样。而且5.x的hibernate必须要生成.hbm.xml文件，否则还是会报错。4.x只需要把生成的实体类在hibernate.cfg.xml中声明即可。
            configuration.addClass(Mobile02Entity.class)
                    .addClass(Mobile021Entity.class);

            serviceRegistry = (ServiceRegistry) new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            ourSessionFactory = configuration.buildSessionFactory((org.hibernate.service.ServiceRegistry) serviceRegistry);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    public static void main(final String[] args) throws Exception {
        final Session session = getSession();
        try {
            session.beginTransaction();
            Mobile021Entity entity = (Mobile021Entity) session.load(Mobile021Entity.class, 1);
            logger.log(Level.FINEST, entity.getContent());
            logger.log(Level.FINEST, entity.getStep());
            logger.log(Level.FINEST, entity.getWriter());
            logger.log(Level.FINEST, entity.getTitle());
            logger.log(Level.FINEST, (Supplier<String>) entity.getTime());


            //因为此的user为persistent状态，所以数据库进行同步
            session.getTransaction().commit();
//下面的代码是IntelliJ IDEA自动生成的，适用于Hibernate4.x,5.x不再可用，执行会报错。
//            System.out.println("querying all the managed entities...");
//            Map<String, ClassMetadata>  map = (Map<String, ClassMetadata>) ourSessionFactory.getAllClassMetadata();
//            for(String entityName : map.keySet()){
//                SessionFactoryImpl sfImpl = (SessionFactoryImpl) ourSessionFactory;
//                String tableName = ((AbstractEntityPersister)sfImpl.getEntityPersister(entityName)).getTableName();
//                System.out.println(entityName + "\t" + tableName);
//            }
//            final Map metadataMap = session.getSessionFactory().getAllClassMetadata();
//            for (Object key : metadataMap.keySet()) {
//                final ClassMetadata classMetadata = (ClassMetadata) metadataMap.get(key);
//                final String entityName = classMetadata.getEntityName();
//                final Query query = session.createQuery("from " + entityName);
//                System.out.println("executing: " + query.getQueryString());
//                for (Object o : query.list()) {
//                    System.out.println("  " + o);
//                }
//            }
        } catch (Exception e) {
            Transaction transaction = session.getTransaction();
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

    }
}