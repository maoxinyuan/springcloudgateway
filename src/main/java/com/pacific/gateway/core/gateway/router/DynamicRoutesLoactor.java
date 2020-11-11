package com.pacific.gateway.core.gateway.router;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.pacific.gateway.common.Constant;
import com.pacific.gateway.common.Context;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author maoxy
 * @date 2019/12/9 20:30
 */
@Component
public class DynamicRoutesLoactor implements RouteDefinitionRepository {

    @ApolloConfig(Constant.ROUTE_NAME)
    private Config config;

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(getApolloRouteDefinitions());
    }

    /* apollomall配置实例
    spring.cloud.gateway.routes.apollomall.id = apollomall
    spring.cloud.gateway.routes.apollomall.url = lb:apollomall-eureka-producer 或 spring.cloud.gateway.routes.apollomall.url = http://IP:port
    spring.cloud.gateway.routes.apollomall.path = "apollomallPath=/apollomall/**,/apollo/**"
    spring.cloud.gateway.routes.apollomall.filter.header = "AddRequestHeader=token,flag"
    spring.cloud.gateway.routes.apollomall.filter.parameter = "AddRequestParameter=compangflag,token"
     */

    private List<RouteDefinition>  getApolloRouteDefinitions(){

        Set<String> propertyNames = config.getPropertyNames();
        Map<String,String> configmap =  new HashMap<String,String>(1);
        propertyNames.stream().forEach(propertyName->{
            configmap.put(propertyName,config.getProperty(propertyName,null));
        });
        Context context = new Context(configmap);
        Map<String, String> data = context.getSubProperties(Constant.GATEWAY_ROUTER_PREFIX);
        List<String> keys = data.keySet().stream().map(s -> s.substring(0, s.indexOf("."))).distinct().collect(Collectors.toList());

        List<RouteDefinition> routeDefinitions = new ArrayList<>();

        keys.stream().forEach(key ->{
            String id = config.getProperty(
                    String.join(".", Constant.GATEWAY_ROUTER_PREFIX, key,Constant.GATEWAY_ROUTER_ID),null);
            String url = config.getProperty(
                    String.join(".", Constant.GATEWAY_ROUTER_PREFIX, key,Constant.GATEWAY_ROUTER_URL),null);
            String predicates = config.getProperty(
                    String.join(".", Constant.GATEWAY_ROUTER_PREFIX, key,Constant.GATEWAY_ROUTER_PREDICATES),null);
            String filterHeader = config.getProperty(
                    String.join(".", Constant.GATEWAY_ROUTER_PREFIX, key,Constant.GATEWAY_ROUTER_FILTER_HEADER),null);
            String filterParameter = config.getProperty(
                    String.join(".", Constant.GATEWAY_ROUTER_PREFIX, key,Constant.GATEWAY_ROUTER_FILTER_PARAMETER),null);

            RouteDefinition definition = new RouteDefinition();
            //定义id
            definition.setId(id);
            //定义url
            URI uri;
            if (url.startsWith(Constant.EUREKA_ROUTER_PREFIX)){
                uri = UriComponentsBuilder.fromOriginHeader(url).build().toUri();
            } else {
                uri = UriComponentsBuilder.fromHttpUrl(url).build().toUri();
            }
            definition.setUri(uri);
            //定义断言
            PredicateDefinition predicate = new PredicateDefinition(predicates);
            definition.setPredicates(Arrays.asList(predicate));
            //定义Filter
            List<FilterDefinition> filters = new ArrayList<>();
            //请求头
            if (null != filterHeader && "" != filterHeader) {
                FilterDefinition filterDefinition = new FilterDefinition(filterHeader);
                filters.add(filterDefinition);
            }
            //请求参数
            if (null != filterParameter && "" != filterParameter){
                FilterDefinition filterDefinition = new FilterDefinition(filterParameter);
                filters.add(filterDefinition);
            }
            definition.setFilters(filters);
            routeDefinitions.add(definition);
        });
        return routeDefinitions;
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }

}
