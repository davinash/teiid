TEIID Connector Arche Type
================

This maven project is the result of being created when the Teiid Connector Archetype is generated.  

When the project is generated, you should have ended up with the following structure:

-  connector-geode
	|-	kits
		|-	wildfly
			|-	docs
				|-	teiid-extensions
					|-	datasources
						|-	connector	
							|-	connector-ds.cli
							|-  connector-ds.xml		
			|-	modules
				|-	org.teiid.resource.adapter.geode
					|-	main
						|-	module.xml
		|-	wildfly-dist.xml 
	| -	pom.xml
	| -	src
		|-	main
			|-	java
				|-	org.teiid.resource.adapter.geode
					|-	geodeConnectionImpl.java
					|-	geodeManagedConnectionFactory.java
					|-	geodePlugin.java
					|-	geodeResourceAdapter.java
			|-	rar
				|-	META-INF
					|-	MANIFEST.MF
					|-	ra.xml
			|-	resources
				|-	org.teiid.resource.adapter.geode
						|-	i18n.properties


Ready to begin adding your custom code.

NOTE:  The MANIFEST.MF will need to have its dependencies updated.  Example, adding
the translator dependency. 

