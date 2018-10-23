package com.github.ohtomi.demo

import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.cloud.netflix.zuul.filters.post.LocationRewriteFilter
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@EnableZuulProxy
@Component
class ZuulProxyConfig {

    // beans -------------------------------------------------------------------

    @Bean
    fun newLocationRewriteFilter(): LocationRewriteFilter = LocationRewriteFilter()
}
