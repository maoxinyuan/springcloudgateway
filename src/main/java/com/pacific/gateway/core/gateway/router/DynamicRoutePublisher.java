package com.pacific.gateway.core.gateway.router;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.pacific.gateway.common.Constant;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * @author maoxy
 * @date 2019/12/9 22:20
 */
@Component
public class DynamicRoutePublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @ApolloConfigChangeListener(value = Constant.ROUTE_NAME)
    public void onChange(ConfigChangeEvent changeEvent) {
        boolean gatewayPropertiesChanged = false;
        for (String changedKey : changeEvent.changedKeys()) {
            if (changedKey.startsWith(Constant.GATEWAY_ROUTER_PREFIX)) {
                gatewayPropertiesChanged = true;
                break;
            }
        }
        if (gatewayPropertiesChanged) {
            publisher.publishEvent(new RefreshRoutesEvent(this));
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

}
