package database.application.development.repository.configuration;

import database.application.development.model.domain.Company;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by HrvojeGrgic on 11/10/2017.
 */
public class ORMConfig {
    private final SessionFactory ourSessionFactory;
    private final ServiceRegistry serviceRegistry;

    @Autowired
    public ORMConfig(){
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            configuration.addAnnotatedClass(Company.class);

            serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            ourSessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    //TODO: delete after implementing jUnit tests
    public static void main(final String[] args) throws Exception {
        final Session session = new ORMConfig().getSession();
        try {
            System.out.println("querying all the managed entities...");
            Company co1 = null;
            final Map metadataMap = session.getSessionFactory().getProperties();

            Transaction transaction = session.beginTransaction();
            co1 = session.get(Company.class,1);
            transaction.commit();

            System.out.println("done...");
        }catch (RuntimeException re){
            re.printStackTrace();
        }
        finally {
            session.close();
            System.exit(-1);
        }
    }

}