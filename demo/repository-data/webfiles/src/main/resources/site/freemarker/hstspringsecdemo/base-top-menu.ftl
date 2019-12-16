<#include "../include/imports.ftl">

<#-- @ftlvariable name="menu" type="org.hippoecm.hst.core.sitemenu.HstSiteMenu" -->
<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#if menu??>
<div class="has-edit-button">
  <#if menu.siteMenuItems??>
    <ul class="nav nav-pills">
      <#list menu.siteMenuItems as item>
        <#if !item.hstLink?? && !item.externalLink??>
          <#if item.selected || item.expanded>
            <li class="active"><div style="padding: 10px 15px;">${item.name?html}</div></li>
          <#else>
            <li><div style="padding: 10px 15px;">${item.name?html}</div></li>
          </#if>
        <#else>
          <#if item.hstLink??>
            <#assign href><@hst.link link=item.hstLink/></#assign>
          <#elseif item.externalLink??>
            <#assign href>${item.externalLink?replace("\"", "")}</#assign>
          </#if>
          <#if  item.selected || item.expanded>
            <li class="active"><a href="${href}">${item.name?html}</a></li>
          <#else>
            <li><a href="${href}">${item.name?html}</a></li>
          </#if>
        </#if>
      </#list>
    </ul>
  </#if>
  <@hst.cmseditmenu menu=menu/>
</div>
</#if>

<#-- Logout must be by POST (see http://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#csrf-logout) -->
<form name="logoutForm" method="post" action="<@hst.link path="/"/>logout">
  <p>
    <#if hstRequest.userPrincipal??>
      Welcome <strong>${hstRequest.userPrincipal.name}</strong>!
      <#if _csrf??>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      </#if>
      <input type="submit" name="logoutButton" value="Log out" />
    </#if>
  </p>
</form>