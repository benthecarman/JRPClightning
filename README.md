
<div align="center">
  <h1>:zap: JRPClightning :zap:</h1>

  <img src="https://github.com/clightning4j/icons/raw/main/org/ic_launcher/res/mipmap-xxxhdpi/ic_launcher.png" />

  <p>
    <strong> :zap: Java framework for C-Lightning to work with the RPC interface, and also the library simplifies the work to develop custom plugins with Java, Kotlin, and all the languages that supports the Java dependencies :zap: </strong>
  </p>

  <p>
   <a href="https://search.maven.org/search?q=g:io.github.clightning4j">
    <img alt="GitHub Workflow Status" src="https://img.shields.io/maven-central/v/io.github.clightning4j/jrpclightning?style=flat-square">
   </a>
   <img alt="GitHub Workflow Status" src="https://img.shields.io/nexus/s/io.github.clightning4j/jrpclightning?server=https%3A%2F%2Foss.sonatype.org&style=flat-square">
    <a href="https://codecov.io/gh/clightning4j/JRPClightning">
    <img alt="GitHub Workflow Status" src="https://codecov.io/gh/clightning4j/JRPClightning/branch/master/graph/badge.svg?token=YVrQVYtqVn">
   </a>
   <a href="https://github.com/clightning4j/JRPClightning/discussions">
    <img alt="GitHub Workflow Status" src="https://img.shields.io/badge/Discussion-Join-green">
   </a>

</p>
</div>

## Table of Content

- Introduction
- How to Use
- Example
- Support
- License

## Introduction
This is a wrapper for c-lightning rpc, this project is inspired by this 
[pull request](https://github.com/ElementsProject/lightning/pull/2223) by @renepickhardt.

The wrapper aims to be versatile, the final version should allow the user to execute commands even of custom plugins, allowing them to write the code for the command.

## How to Use

#### Maven

```xml
<dependency>
  <groupId>io.github.clightning4j</groupId>
  <artifactId>jrpclightning</artifactId>
  <version>0.2.1</version>
</dependency>
```

#### Gradle Kotlin DSL

```kotlin
implementation("io.github.clightning4j:jrpclightning:0.2.1")
```

#### Gradle groovy DSL

```groovy
implementation 'io.github.clightning4j:jrpclightning:0.2.1'
```


#### sbt

```sbt
libraryDependencies += "io.github.clightning4j" % "jrpclightning" % "0.2.1"
```

```groovy
implementation 'io.github.clightning4j:jrpclightning:0.2.1'
```

### Snapshot version

Each master version has a SNAPSHOT version that is the official version `x.x.x + 1`, so for example for the version `v0.1.9`
the version on if exist a new version of the master branch is `v0.1.10-SNAPSHOT`, or some release candidate version, like `v0.1.10-rc1`, will be
`v0.1.10-rc1-SNAPSHOT`.

Describe a static rule at the moment it is difficult for the snapshot release, for this reason, feel free to open a 
[question on the Github discussion](https://github.com/clightning4j/JRPClightning/discussions) if you have any doubt

An example of gradle configuration is reported below

_Gradle (Kotlin DSL)_

```kotlin
configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(0, "seconds")
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

repositories {
    ... other suff
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    ... other stuff 
    implementation("io.github.clightning4j:jrpclightning:0.1.9-SNAPSHOT")
}

```

### Command Support

At the moment the library doesn't support all command available on c-lightning, a list of commands is described inside [the javadoc](https://vincenzopalazzo.github.io/JRPClightning/)

- You can use the RPC wrapper to call the method **getinfo**, that returns the 
json wrapper, called CLightningGetInfo.
    
    ```java
        CLightningGetInfo infoNode = CLightningRPC.getInstance().getInfo();
        String id = infoNode.getId();
        String color = infoNode.getColor();
        System.out.println("id=" + id);
        System.out.println("color=" + color);
    ```

- You can create a personal wrapper to support new commands (i.e: personal plugins or command not supported yet by the library)
   
  **Create the command wrapper**
  
  ```java
  public class PersonalDelPayRPCCommand extends AbstractRPCCommand<CLightningListPays> {
  
      private static final String COMMAND_NAME = "delpay";
  
      public PersonalDelPayRPCCommand() {
          super(COMMAND_NAME);
      }
  
      @Override
      protected Type toTypeFromClass() {
          return new TypeToken<RPCResponseWrapper<CLightningListPays>>(){}.getType();
      }
  }
  ```
  
  **Create the personal Json wrapper** 
  - In these cases it is used a library wrapper, you can see all wrappers available in the library [here](https://vincenzopalazzo.github.io/JRPClightning/)
  
  **Register the command and run it**
  
  ```java
  PersonalRPCCommand paysCommand = new PersonalRPCCommand();
  CLightningRPC.registerCommand(CustomCommand.DELPAY, paysCommand);
  CLightningListPay result = CLightningRPC.runRegisterCommand(CustomCommand.DELPAY, payload);
  ```

Complete javadoc [here](https://vincenzopalazzo.github.io/JRPClightning/).

### Configuration Unix Socket
For the configuration of unix socket you can create a file called **clightning-rpc.properties** inside the classpath in your application.

The example file config is [here](https://github.com/vincenzopalazzo/JRPClightning/blob/master/src/main/resources/clightning-rpc.properties).

```
## The CLightning rpc wrapper configurator

RPC_DIR=/media/vincenzo/Maxtor/C-lightning/node/testnet/lightning-rpc
```

### Plugin support

The library supports plugins from version 0.1.8, and the library contains a collections of Annotation to make the developing phase easy.

## Examples

##### Create a rpc method in a Java class

```java
    @RPCMethod(
            name = "annotation_hello",
            description = "Annotation plugin"
    )
    public void hello(CLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response) {
        log(CLightningLevelLog.WARNING, request.toString());
        response.add("type", "random");
    }
```

##### Receive a notification in a Java

```java
    @Subscription(notification = "invoice_creation")
    public void doInvoiceCreation(CLightningJsonObject data){
        log(CLightningLevelLog.WARNING, "Notification invoice_creation received inside the plugin lightning rest");
        CLightningLogger.getInstance().debug( this.getClass(), "Data received by notification are \n" + data.toString());
    }
```

##### Subscript to a hooks from a Java class

```java
    @Hook(hook = "rpc_command")
    public void logAllRPCCommand(CLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response) {
        log(CLightningLevelLog.WARNING, request.toString());
        response.add("result", "continue");
    }
```

### Kotlin alternative

##### Create  plugin in Koltin

```kotlin
class Plugin : CLightningPlugin() {

    @PluginOption(
        name = "hello-kotlin",
        description = "This propriety is a fake propriety, there is any problem if it is not exist in the command line",
        defValue = "true",
        typeValue = "flag"
    )
    private var sayHello = false
}
```

A complete example of a plugin wrote with kotlin is available at the [following link](https://github.com/clightning4j/btcli4j) or a java version
it is [available at the following link](https://github.com/clightning4j/lightning-rest).

Start to write a plugin with the following templates

- [Kotlin Template](https://github.com/clightning4j/kotlin-template)

## Support
If you like the library and want to support it, please considerer to donate with the following systems

- [3BQ8qbn8hLdmBKEjt1Hj1Z6SiDsnjJurfU](bitcoin:3BQ8qbn8hLdmBKEjt1Hj1Z6SiDsnjJurfU)
- [liberapay.com/vincenzopalazzo](https://liberapay.com/vincenzopalazzo)
- [Github support](https://github.com/sponsors/vincenzopalazzo)
- [buymeacoffee](https://www.buymeacoffee.com/vincenzopalazzo)

P.S: Lightning donation is coming ;-)

## License

<div align="center">
  <img src="https://opensource.org/files/osi_keyhole_300X300_90ppi_0.png" width="150" height="150"/>
</div>

 C-lightning RPC wrapper, with the complete support to Plugins and custom settings.

 Copyright (C) 2020-2021 Vincenzo Palazzo vincenzopalazzodev@gmail.com
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 
