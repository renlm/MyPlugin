package ${package.ServiceImpl};

import org.springframework.stereotype.Service;

<#if cfg.dsName?default("")?trim?length gt 1>import ${cfg.nameOfDS};
</#if>import ${superServiceImplClassPackage};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};

/**
 * <p>
 * ${table.comment!} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
<#if cfg.dsName?default("")?trim?length gt 1>@DS("${cfg.dsName}")
</#if><#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {

}
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${table.serviceName} {

}
</#if>
