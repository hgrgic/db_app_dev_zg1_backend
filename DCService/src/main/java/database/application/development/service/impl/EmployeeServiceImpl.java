package database.application.development.service.impl;

import database.application.development.model.domain.Employee;
import database.application.development.model.history.HstEmployee;
import database.application.development.model.messages.ApplicationInputs;
import database.application.development.model.messages.OutputHeader;
import database.application.development.model.messages.Request;
import database.application.development.model.messages.Response;
import database.application.development.repository.EmployeeDao;
import database.application.development.repository.configuration.ORMConfig;
import database.application.development.repository.hst.HstEmployeeDao;
import database.application.development.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@Slf4j
public class EmployeeServiceImpl extends ORMConfig implements EmployeeService {

    private EmployeeDao employeeDao;
    private HstEmployeeDao hstEmployeeDao;

    @Autowired
    public EmployeeServiceImpl(EmployeeDao employeeDao, HstEmployeeDao hstEmployeeDao) {
        super();
        this.employeeDao = employeeDao;
        this.hstEmployeeDao = hstEmployeeDao;
    }

    @Override
    public Response<Employee> getEmployeeById(Request<ApplicationInputs> request) {
        Session session = this.getSession();
        Employee employee = employeeDao.getEmployeeById(request.getBody().getEntityId(), session);
        return new Response<>(new OutputHeader(), employee);
    }

    @Override
    public Response<Employee> createEmployee(Request<ApplicationInputs> request) {
        Session session = this.getSession();
        session.beginTransaction();
        Employee employee = employeeDao.createEmployee(request.getBody().getEmployee(), session);
        addToEmployeeHistory("INSERT", employee, session);
        session.getTransaction().commit();
        session.close();
        return new Response<>(new OutputHeader(), employee);
    }

    @Override
    public Response<Employee> updateEmployee(Request<ApplicationInputs> request) {
        Session session = this.getSession();
        session.beginTransaction();
        Employee employee = employeeDao.updateEmployee(request.getBody().getEmployee(), session);
        addToEmployeeHistory("UPDATE", employee, session);
        session.getTransaction().commit();
        session.close();
        return new Response<>(new OutputHeader(), employee);
    }

    @Override
    public Response<Employee> findByUsername(Request<ApplicationInputs> request) {
        Session session = this.getSession();
        Employee employee = employeeDao.findByEmail(request.getBody().getEmployee(), session);

        return new Response<>(new OutputHeader(), employee);
    }

    @Override
    public void deleteEmployee(Request<ApplicationInputs> request) {
        Session session = this.getSession();
        session.beginTransaction();
        Employee employee = employeeDao.getEmployeeById(request.getBody().getEntityId(), session);
        addToEmployeeHistory("DELETE", employee, session);
        employeeDao.deleteEmployee(employee, session);
        session.getTransaction().commit();
        session.close();
    }

    /**
     * Adds a new row to the HST_EMPLOYEE table for this employee object.
     *
     * @param changeDesc The description of the change (INSERT, UPDATE, or DELETE)
     * @param employee The {@link Employee} object which has been changed
     */
    private void addToEmployeeHistory(String changeDesc, Employee employee, Session session) {
        HstEmployee hstEmployee = new HstEmployee(changeDesc, employee);
        hstEmployee = hstEmployeeDao.createHstEmployee(hstEmployee, session);
        if(employee.getHstEmployees() == null) employee.setHstEmployees(new HashSet<HstEmployee>());
        employee.getHstEmployees().add(hstEmployee);
    }
}
