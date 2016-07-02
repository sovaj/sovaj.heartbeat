<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="application-test">
    <xsl:choose>
      <xsl:when test="dependency-tests/@status = 'KO'">images/warning.gif</xsl:when>
      <xsl:when test="technical-tests/@status = 'OK'">images/check.gif</xsl:when>
      <xsl:otherwise>images/error.gif</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>