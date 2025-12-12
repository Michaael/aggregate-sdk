<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" encoding="UTF-8" indent="yes" />
	<xsl:template match="table">
		<table border="1">
			<tr bgcolor="silver">
				<xsl:for-each select="format/fields/field">
					<td>
						<xsl:choose>
							<xsl:when test="@description">
								<xsl:value-of select="@description" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@name" />
							</xsl:otherwise>
						</xsl:choose>
					</td>
				</xsl:for-each>
			</tr>
			<xsl:for-each select="records/record">
				<xsl:call-template name="printRecord">
					<xsl:with-param name="rec" select="self::*" />
					<xsl:with-param name="fields"
						select="parent::*/parent::table/format/fields" />
				</xsl:call-template>
			</xsl:for-each>
		</table>
	</xsl:template>
	<xsl:template name="printRecord">
		<xsl:param name="rec" select="0" />
		<xsl:param name="fields" select="0" />
		<tr>
			<xsl:for-each select="$fields/child::field">
				<xsl:choose>
					<xsl:when test="$rec/value[@name=current()/@name]">
						<xsl:call-template name="exposeFieldValue">
							<xsl:with-param name="valueNode"
								select="$rec/value[@name=current()/@name]" />
							<xsl:with-param name="fieldNode" select="self::*" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="exposeFieldValue">
							<xsl:with-param name="valueNode" select="child::defaultValue" />
							<xsl:with-param name="fieldNode" select="self::*" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</tr>
	</xsl:template>
	<xsl:template name="exposeFieldValue">
		<xsl:param name="valueNode" select="0" />
		<xsl:param name="fieldNode" select="0" />
		<td>
			<xsl:choose>
				<xsl:when test="$fieldNode/child::selectionValues">
					<xsl:choose>
						<xsl:when
							test="$fieldNode/selectionValues/option[text()=string($valueNode)]">
							<xsl:value-of
								select="$fieldNode/selectionValues/option[text()=string($valueNode)]/@description" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$valueNode" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="$fieldNode/@type='B'">
							<xsl:choose>
								<xsl:when test="$valueNode/text()='1'">
									<xsl:element name="font">
										<xsl:attribute name="color">green</xsl:attribute>
										<xsl:text>true</xsl:text>
									</xsl:element>
								</xsl:when>
								<xsl:otherwise>
									<xsl:element name="font">
										<xsl:attribute name="color">red</xsl:attribute>
										<xsl:text>false</xsl:text>
									</xsl:element>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:when test="$fieldNode/@type='T'">
							<xsl:apply-templates select="$valueNode/child::*" />
						</xsl:when>
						<xsl:when test="$fieldNode/@type='A'">
							<xsl:apply-templates select="$valueNode/child::data/@name" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="not($valueNode) or not($valueNode/text())">
									<xsl:choose>
										<xsl:when
											test="$fieldNode/@nullable and $fieldNode/@nullable='true'">
											<font color="blue">Null</font>
										</xsl:when>
										<xsl:otherwise>
											<br />
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$valueNode" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</xsl:template>
</xsl:stylesheet>
