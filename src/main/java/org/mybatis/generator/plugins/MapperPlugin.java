//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class MapperPlugin extends FalseMethodPlugin {
    private Set<String> mappers = new HashSet();
    private boolean caseSensitive = false;
    private boolean useMapperCommentGenerator = true;
    private String beginningDelimiter = "";
    private String endingDelimiter = "";
    private String schema;
    private CommentGeneratorConfiguration commentCfg;
    private boolean forceAnnotation;

    public MapperPlugin() {
    }

    public String getDelimiterName(String name) {
        StringBuilder nameBuilder = new StringBuilder();
        if (StringUtility.stringHasValue(this.schema)) {
            nameBuilder.append(this.schema);
            nameBuilder.append(".");
        }

        nameBuilder.append(this.beginningDelimiter);
        nameBuilder.append(name);
        nameBuilder.append(this.endingDelimiter);
        return nameBuilder.toString();
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        Iterator var5 = this.mappers.iterator();

        while (var5.hasNext()) {
            String mapper = (String) var5.next();
            interfaze.addImportedType(new FullyQualifiedJavaType(mapper));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(mapper + "<" + entityType.getShortName() + ">"));
        }

        interfaze.addImportedType(entityType);
        return true;
    }

    protected void processEntityClass(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("javax.persistence.*");
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        if (StringUtility.stringContainsSpace(tableName)) {
            tableName = this.context.getBeginningDelimiter() + tableName + this.context.getEndingDelimiter();
        }

        if (this.caseSensitive && !topLevelClass.getType().getShortName().equals(tableName)) {
            topLevelClass.addAnnotation("@Table(name = \"" + this.getDelimiterName(tableName) + "\")");
        } else if (!topLevelClass.getType().getShortName().equalsIgnoreCase(tableName)) {
            topLevelClass.addAnnotation("@Table(name = \"" + this.getDelimiterName(tableName) + "\")");
        } else if (!StringUtility.stringHasValue(this.schema) && !StringUtility.stringHasValue(this.beginningDelimiter) && !StringUtility.stringHasValue(this.endingDelimiter)) {
            if (this.forceAnnotation) {
                topLevelClass.addAnnotation("@Table(name = \"" + this.getDelimiterName(tableName) + "\")");
            }
        } else {
            topLevelClass.addAnnotation("@Table(name = \"" + this.getDelimiterName(tableName) + "\")");
        }

    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.processEntityClass(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.processEntityClass(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.processEntityClass(topLevelClass, introspectedTable);
        return false;
    }

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        this.useMapperCommentGenerator = !"FALSE".equalsIgnoreCase(context.getProperty("useMapperCommentGenerator"));
        if (this.useMapperCommentGenerator) {
            this.commentCfg = new CommentGeneratorConfiguration();
            this.commentCfg.setConfigurationType(MapperCommentGenerator.class.getCanonicalName());
            context.setCommentGeneratorConfiguration(this.commentCfg);
        }

        context.getJdbcConnectionConfiguration().addProperty("remarksReporting", "true");
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String mappers = this.properties.getProperty("mappers");
        if (!StringUtility.stringHasValue(mappers)) {
            throw new RuntimeException("Mapper插件缺少必要的mappers属性!");
        } else {
            String[] var3 = mappers.split(",");
            int var4 = var3.length;

            String endingDelimiter;
            for (int var5 = 0; var5 < var4; ++var5) {
                endingDelimiter = var3[var5];
                this.mappers.add(endingDelimiter);
            }

            String caseSensitive = this.properties.getProperty("caseSensitive");
            if (StringUtility.stringHasValue(caseSensitive)) {
                this.caseSensitive = caseSensitive.equalsIgnoreCase("TRUE");
            }

            String forceAnnotation = this.properties.getProperty("forceAnnotation");
            if (StringUtility.stringHasValue(forceAnnotation)) {
                if (this.useMapperCommentGenerator) {
                    this.commentCfg.addProperty("forceAnnotation", forceAnnotation);
                }

                this.forceAnnotation = forceAnnotation.equalsIgnoreCase("TRUE");
            }

            String beginningDelimiter = this.properties.getProperty("beginningDelimiter");
            if (StringUtility.stringHasValue(beginningDelimiter)) {
                this.beginningDelimiter = beginningDelimiter;
            }

            endingDelimiter = this.properties.getProperty("endingDelimiter");
            if (StringUtility.stringHasValue(endingDelimiter)) {
                this.endingDelimiter = endingDelimiter;
            }

            String schema = this.properties.getProperty("schema");
            if (StringUtility.stringHasValue(schema)) {
                this.schema = schema;
            }

            if (this.useMapperCommentGenerator) {
                this.commentCfg.addProperty("beginningDelimiter", this.beginningDelimiter);
                this.commentCfg.addProperty("endingDelimiter", this.endingDelimiter);
            }

        }
    }
}
