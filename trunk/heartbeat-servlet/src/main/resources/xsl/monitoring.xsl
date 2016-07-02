<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<html>
			<header>
				<style>
					body{
					font-family:Helvetica,Arial,sans-serif;
					font-style:normal;
					}
					table{
					border:solid 1px #003366;
					margin:0;
					}
					td, th
					{
					padding: 3px;
					border:solid 1px #003366;
					}
					td{
					font-size:12px;
					}
					th {
					font-weight:bold;
					background-color:#3C78B5;
					font-size:14px;
					color:white;
					}
 </style>
			<xsl:for-each select="/application-test">
					 <title>Monitoring : <xsl:value-of select="@webAppName" /></title>
			</xsl:for-each>		 
			</header>
			<body>
				<xsl:for-each select="/application-test">
				
					<table style="border:0px; bordercolor:white;" border="0"  cellpadding="0" cellspacing="0">
			<tr>
			<td align="center" >
				
					<table align="center" cellspacing="0">
						<tr>
							<th>Application Name</th>
							<th>Node Name</th>
							<th>Server Name</th>
							<th>Technical Status</th>
							<th>Dependency Status</th>
						</tr>
						<tr>
							<td>
								<b>
									<xsl:value-of select="@webAppName" />
								</b>
							</td>
							<td>
								<b>
									<xsl:value-of select="@nodeName" />
								</b>
							</td>
							<td>
								<b>
									<xsl:value-of select="@appServerName" />
								</b>
							</td>
							<td>
								<xsl:for-each select="./technical-tests">
									<xsl:call-template name="display_status"  />
								</xsl:for-each>
							</td>
							<td>
								<xsl:for-each select="./dependency-tests">
									<xsl:call-template name="display_status" />
								</xsl:for-each>
							</td>
						</tr>
					</table>
					
				</td>
</tr>

<tr>

<td align="center">
				
					<br />
					<xsl:call-template name="display_application-test" />
					
			</td>
</tr>			
</table>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>

	<xsl:template name="display_application-test">
		<table cellspacing="0">
			<tr>
				<th>Name</th>
				<th>Type</th>
				<th>Description</th>
				<th>Status</th>
				<th>Duration</th>
				<th>Error</th>
			</tr>
			<xsl:for-each select="./technical-tests">
				<xsl:call-template name="display_technical-tests" />
			</xsl:for-each>
			<xsl:for-each select="./dependency-tests">
				<xsl:call-template name="display_dependency-tests" />
			</xsl:for-each>
		</table>
	</xsl:template>

	<xsl:template name="display_technical-tests">
		<tr>
			<td colspan="6" bgcolor="turquoise" align="center">
				<b>Technical Tests</b>
			</td>
		</tr>
		<xsl:for-each select="./test">
			<xsl:call-template name="display_test" />
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="display_dependency-tests">
		<tr>
			<td colspan="6" bgcolor="turquoise" align="center">
				<b>Dependency Tests</b>
			</td>
		</tr>
		<xsl:for-each select="./test">
			<xsl:call-template name="display_test" />
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="display_test">
		<tr>
			<td>
				<b>
					<xsl:value-of select="@name" />
				</b>
			</td>
			<td>
				<xsl:value-of select="@type" />
			</td>
			<td>
				<xsl:value-of select="description/text()" />
			</td>
			<td>
				<xsl:attribute name="bgcolor">
			<xsl:choose>
	  			<xsl:when test="@status = 'KO'">red</xsl:when>
  				<xsl:otherwise>#00FF00</xsl:otherwise>
			</xsl:choose>
			</xsl:attribute>
				<xsl:value-of select="@status" />
			</td>
			<td>
				<xsl:value-of select="@duration" />
				ms
			</td>
			<td>
				<xsl:value-of select="errorMessage/text()" />
				.
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="display_status">
		<xsl:attribute name="bgcolor">
			<xsl:choose>
	  			<xsl:when test="@status = 'KO'">red</xsl:when>
  				<xsl:otherwise>#00FF00</xsl:otherwise>
			</xsl:choose>
			</xsl:attribute>
		<xsl:choose>
			<xsl:when test="@status = 'KO'">
				<b>
					<xsl:value-of select="@status" />
				</b>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@status" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>