package database.application.development.service.impl;

import database.application.development.model.domain.Company;
import database.application.development.model.domain.RewardPolicy;
import database.application.development.model.messages.ApplicationInputs;
import database.application.development.model.messages.OutputHeader;
import database.application.development.model.messages.Request;
import database.application.development.model.messages.Response;
import database.application.development.repository.CompanyDao;
import database.application.development.repository.RewardPolicyDao;
import database.application.development.repository.configuration.ORMConfig;
import database.application.development.service.RewardPolicyService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RewardPolicyServiceImpl extends ORMConfig implements RewardPolicyService{

    private RewardPolicyDao rewardPolicyDao;
    private CompanyDao companyDao;

    @Autowired
    public RewardPolicyServiceImpl(RewardPolicyDao rewardPolicyDao, CompanyDao companyDao) {
        super();
        this.rewardPolicyDao = rewardPolicyDao;
        this.companyDao = companyDao;
    }

    @Override
    public Response<RewardPolicy> getRewardPolicyById(Request<ApplicationInputs> request) {
        Session session = this.getSession();
        RewardPolicy rewardPolicy = rewardPolicyDao.getRewardPolicyById(request.getBody().getEntityId(), session);
        return new Response<>(new OutputHeader(), rewardPolicy);
    }

    @Override
    public Response<RewardPolicy> createRewardPolicy(Request<ApplicationInputs> request) {
        Session session = this.getSession();
        session.beginTransaction();
        RewardPolicy rewardPolicy = rewardPolicyDao.createRewardPolicy(request.getBody().getRewardPolicy(), session);

        session.getTransaction().commit();
        session.close();
        return new Response<>(new OutputHeader(), rewardPolicy);
    }

    @Override
    public Response<RewardPolicy> updateRewardPolicy(Request<ApplicationInputs> request) {
        Session session = this.getSession();
        session.beginTransaction();
        RewardPolicy rewardPolicy = rewardPolicyDao.updateRewardPolicy(request.getBody().getRewardPolicy(), session);

        session.getTransaction().commit();
        session.close();
        return new Response<>(new OutputHeader(), rewardPolicy);
    }

    @Override
    public void deleteRewardPolicy(Request<ApplicationInputs> request) {
        Session session = this.getSession();
        session.beginTransaction();
        Company company = companyDao.getCompanyById(request.getBody().getCompanyId(), session);
        RewardPolicy rewardPolicy = rewardPolicyDao.getRewardPolicyById(request.getBody().getEntityId(), session);

        companyDao.deleteRewardPolicy(company, rewardPolicy, session);
        session.getTransaction().commit();
        session.close();
    }

}
