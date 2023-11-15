package tz.go.mof.trab.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import tz.go.mof.trab.models.*;

@Configuration
public class RestJpaConfig extends RepositoryRestConfigurerAdapter {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Bill.class);
        config.exposeIdsFor(Appeals.class);
        config.exposeIdsFor(Region.class);
        config.exposeIdsFor(Summons.class);
        config.exposeIdsFor(Notice.class);
        config.exposeIdsFor(Adress.class);
        config.exposeIdsFor(Payment.class);
        config.exposeIdsFor(BaseEntity.class);
        config.exposeIdsFor(Judge.class);
        config.exposeIdsFor(TaxType.class);
        config.exposeIdsFor(Taxes.class);
        config.exposeIdsFor(Appeals.class);
        config.exposeIdsFor(ApplicationRegister.class);
        config.exposeIdsFor(AppealStatusTrend.class);
    }
}