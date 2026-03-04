<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:maven="http://maven.apache.org/POM/4.0.0">

    <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:strip-space elements="*"/>
    <xsl:preserve-space elements="project"/>

    <!-- Copy everything by default -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- Ensure newline after XML declaration -->
    <xsl:template match="/">
        <xsl:text>&#10;</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>

    <!-- Add double newlines to improve readability -->
    <xsl:template match="maven:packaging | maven:properties | maven:dependencyManagement | maven:project/maven:dependencies | maven:project/maven:build | maven:profiles | maven:project/maven:repositories | maven:project/maven:parent">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
        <xsl:text>&#10;&#10;</xsl:text>
    </xsl:template>

    <!-- Remove specific plugins -->
    <xsl:template match="maven:plugin[maven:artifactId='spotless-maven-plugin']"/>
    <!-- Remove specific dependencies -->
    <xsl:template match="maven:dependency[maven:artifactId='hilla-spring-boot-starter']"/>

    <xsl:template match="maven:dependency[maven:artifactId='spring-boot-starter-data-jpa']"/>
    <xsl:template match="maven:dependency[maven:artifactId='h2']"/>
    <xsl:template match="maven:dependency[maven:artifactId='spring-boot-starter-validation']"/>
    <xsl:template match="maven:dependency[maven:artifactId='spring-boot-starter-actuator']"/>

</xsl:stylesheet>