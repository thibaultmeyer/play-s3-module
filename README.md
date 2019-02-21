# Play S3 Module


[![Latest release](https://img.shields.io/badge/latest_release-19.01-orange.svg)](https://github.com/thibaultmeyer/play-s3-module/releases)
[![JitPack](https://jitpack.io/v/thibaultmeyer/play-s3-module.svg)](https://jitpack.io/#thibaultmeyer/play-s3-module)
[![Build](https://api.travis-ci.org/thibaultmeyer/play-s3-module.svg)](https://travis-ci.org/thibaultmeyer/play-s3-module)
[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/thibaultmeyer/play-s3-module/master/LICENSE)

Amazon S3 module for Play Framework 2
*****

## Add play-s3-module to your project

#### build.sbt

     resolvers += "jitpack" at "https://jitpack.io"

     libraryDependencies += "com.github.thibaultmeyer" % "play-s3-module" % "release~YY.MM"

#### application.conf

    ## Amazon AWS
    # ~~~~~
    aws {
      authKey = "your-access-key"
      authSecret = "your-secret-key"
      
      # Amazon S3 Plugin
      # ~~~~~
      # https://github.com/thibaultmeyer/play-s3-module
      s3 {
        endPoint = "s3-eu-west-1.amazonaws.com"
        signingRegion = "eu-west-1"
        withPathStyle = false
        disableChunkedEncoding = false
        bucketName = "your-bucket"
        publicUrl = "https://s3-eu-west-1.amazonaws.com/"
      }
    }

    ## Ebean
    # https://github.com/payintech/play-ebean
    # ~~~~~
    ebean {
      servers {
        default {
          enhancement = ["models.*", "com.zero_x_baadf00d.play.module.aws.s3.ebean.S3FileModel"]
        }
      }
    }




## Usage

#### Example 1

```java
    public class MyController extends Controller {

        public Result index() {
            // Do something with PlayS3.getAmazonS3()
            return ok();
        }
    }
```


#### Example 2

```java
    public class MyController extends Controller {

        public Result index() {
            final File avatar = new File("/tmp/avatar.png");
            final S3FileModel s3avatar = new S3FileModel();
            s3avatar.setName("avatar.png");
            s3avatar.setSubDirectory("account-avatar");
            s3avatar.setObject(avatar);
            s3avatar.setContentType("image/png");
            s3avatar.save();
            return ok(s3avatar.getUrlAsString());
        }
    }
```



## License
This project is released under terms of the [MIT license](https://raw.githubusercontent.com/thibaultmeyer/play-s3-module/master/LICENSE).
