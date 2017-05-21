# imagingbook-common

This repository provides Java source code supplementing
the digital image processing books by W. Burger & M. J. Burge
(see [imagingbook.com](https://imagingbook.com) for details).

Main repository: [imagingbook-public](https://github.com/imagingbook/imagingbook-public)

JavaDoc: https://imagingbook.github.io/imagingbook-common/javadoc/

## Maven setup

To use the ``imagingbook-common`` library in your Maven project, add the following lines to your ``pom.xml`` file:
````
<repositories>
    <repository>
	<id>imagingbook-maven-repository</id>
    	<url>https://raw.github.com/imagingbook/imagingbook-maven-repository/master</url>
    	<layout>default</layout>
    </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.imagingbook</groupId>
    <artifactId>imagingbook-common</artifactId>
    <version>1.5</version>
  </dependency>
  <!-- other dependencies ... -->
</dependencies>
````
