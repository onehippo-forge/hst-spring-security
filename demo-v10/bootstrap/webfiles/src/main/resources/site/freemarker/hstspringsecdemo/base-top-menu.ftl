<#include "../include/imports.ftl">

<#-- @ftlvariable name="menu" type="org.hippoecm.hst.core.sitemenu.HstSiteMenu" -->
<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#if menu??>
  <#if menu.siteMenuItems??>
    <ul class="nav nav-pills">
      <#list menu.siteMenuItems as item>
        <#if  item.selected || item.expanded>
          <li class="active"><a href="<@hst.link link=item.hstLink/>">${item.name?html}</a></li>
        <#else>
          <li><a href="<@hst.link link=item.hstLink/>">${item.name?html}</a></li>
        </#if>
      </#list>
    </ul>
  </#if>
  <@hst.cmseditmenu menu=menu/>
<#else>
  <#if editMode>
    <h5>[Menu Component]</h5>
    <sub>Click to edit Menu</sub>
  </#if>
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
