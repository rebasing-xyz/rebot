## ReBot Container Image


#### Pre requisites

Before to proceed to the next steps we need to install a few of necessary tools, which are:

 - cekit: Container image creation tool
 - docker-squash: Not mandatory but nice to have, this tool will remove unnecessary image layers decreasing its size.
 
 
#### Installing cekit:
 
To install follow the steps described here: https://github.com/cekit/cekit#installation
 
 
#### Installing docker-squash:
 
To install follow the steps described here: https://github.com/goldmann/docker-squash#installation
  
  

### Building the ReBot image

After install the needed tools, build the image is a easy step, just execute the following command:


```bash
$ cekit build -v
2018-06-26 11:28:46,542 cekit        DEBUG    Running version 2.0.0
2018-06-26 11:28:46,543 cekit        INFO     Generating files for docker engine.
2018-06-26 11:28:46,543 cekit        DEBUG    Loading descriptor from path 'image.yaml'.
2018-06-26 11:28:46,569 cekit        INFO     Initializing image descriptor...
2018-06-26 11:28:46,569 cekit        DEBUG    Retrieving module repositories for 'rebaseit/rebot-telegram-bot'
2018-06-26 11:28:46,570 cekit        DEBUG    Fetching resource 'modules'

....

2018-06-26 11:29:03,270 cekit        INFO     Rendering Dockerfile...
2018-06-26 11:29:03,311 cekit        DEBUG    Dockerfile rendered
2018-06-26 11:29:03,312 cekit        INFO     Using Docker builder to build the image.
2018-06-26 11:29:03,393 cekit        DEBUG    Building image with tags: 'rebaseit/rebot-telegram-bot:0.2', 'rebaseit/rebot-telegram-bot:latest'
2018-06-26 11:29:03,393 cekit        INFO     Building container image...
2018-06-26 11:29:03,393 cekit        DEBUG    Running Docker build: 'docker build -t rebaseit/rebot-telegram-bot:0.2 -t rebaseit/rebot-telegram-bot:latest target/image'
Sending build context to Docker daemon 188.3 MB
Step 1/17 : FROM docker.io/openjdk:latest
Trying to pull repository docker.io/library/openjdk ... 
sha256:8e2c6b93e023efaaab8569cbbfcc3d212fe626ea2b5f28de0bd98ce939c68254: Pulling from docker.io/library/openjdk

...

Successfully built 54e35bc565d3
2018-06-26 11:32:06,615 cekit        INFO     Image built and available under following tags: rebaseit/rebot-telegram-bot:0.2, rebaseit/rebot-telegram-bot:latest
2018-06-26 11:32:06,616 cekit        INFO     Finished!

```


As result, we have a new image *rebaseit/rebot-telegram-bot rebaseit/rebot-telegram-bot* under the tags **0.2** and **latest**


You can check it by running the following command:

```bash
$ docker images
REPOSITORY                       TAG                 IMAGE ID            CREATED             SIZE
rebaseit/rebot-telegram-bot      0.2                 54e35bc565d3        2 minutes ago       1.19 GB
rebaseit/rebot-telegram-bot      latest              54e35bc565d3        2 minutes ago       1.19 GB

```

You can now run the image:

```bash
$ docker run -it --env REBOT_TELEGRAM_TOKEN_ID=YOUR_TOKEN_ID --env REBOT_TELEGRAM_USER_ID=YOUR_BOT_USER_ID \ 
--env REBOT_TELEGRAM_CHAT_ID=YOUR_CHAT_ID --env REBOT_TELEGRAM_LOG_LEVEL=trace rebaseit/rebot:latest

Bot correctly configured.
Running ReBot image with the following configurations:
REBOT_TELEGRAM_TOKEN_ID: YOUR_TOKEN_ID
REBOT_TELEGRAM_USER_ID: YOUR_BOT_USER_ID
REBOT_TELEGRAM_CHAT_ID: YOUR_CHAT_ID
REBOT_TELEGRAM_LOG_LEVEL: trace

...

INFO  [it.rebase.rebot.Startup] (ServerService Thread Pool -- 11) YOUR_BOT_USER_ID successfully started.
```


### Squashing the image

If you check the image size you will notice that its size is **1.19 GB**.

It is recommended squash it before publish the image, for example, in the docker hub.

To squash the image follow the next steps:


Get the image id
```bash
$ docker images
REPOSITORY                       TAG                 IMAGE ID            CREATED             SIZE
rebaseit/rebot-telegram-bot      0.2                 54e35bc565d3        2 minutes ago       1.19 GB
rebaseit/rebot-telegram-bot      latest              54e35bc565d3        2 minutes ago       1.19 GB
```

In this case the image ID is **54e35bc565d3**, with the id in hands we can now squase it:


```bash
$ docker-squash 54e35bc565d3
shell-init: error retrieving current directory: getcwd: cannot access parent directories: No such file or directory
2018-06-26 11:53:22,073 root         INFO     docker-squash version 1.0.8rc1.dev, Docker 63758e0-unsupported, API 1.26...
2018-06-26 11:53:22,073 root         INFO     Using v2 image format
2018-06-26 11:53:22,092 root         INFO     Old image has 31 layers
2018-06-26 11:53:22,092 root         INFO     Checking if squashing is necessary...
2018-06-26 11:53:22,092 root         INFO     Attempting to squash last 31 layers...
2018-06-26 11:53:22,092 root         INFO     Saving image sha256:54e35bc565d32b59c95b6b1cb088f4274a86c60a6175c95e29500ae89b8553c9 to /tmp/docker-squash-HvmLAk/old directory...
2018-06-26 11:54:11,170 root         INFO     Image saved!
2018-06-26 11:54:11,173 root         INFO     Squashing image '54e35bc565d3'...
2018-06-26 11:54:11,178 root         INFO     Starting squashing...
2018-06-26 11:54:11,179 root         INFO     Squashing file '/tmp/docker-squash-HvmLAk/old/3be29e356418770588fde87e9fd45ec297d6203fe5b6ceb242f5cd4e656362ee/layer.tar'...

...

2018-06-26 12:01:26,145 root         INFO     Squashing finished!
2018-06-26 12:01:29,294 root         INFO     Original image size: 1152.12 MB
2018-06-26 12:01:29,294 root         INFO     Squashed image size: 784.33 MB
2018-06-26 12:01:29,294 root         INFO     Image size decreased by 31.92 %
2018-06-26 12:01:29,294 root         INFO     New squashed image ID is f3018f69c64aa5beae168e16b9583ca8de715afa4e63c826f971180f1708dc41
2018-06-26 12:01:43,995 root         INFO     Done
```

Note that a new image was generated and its size decreased from **1152.12 MB** to **784.33 MB**, new we also need to tag the new image:

```bash
docker tag f3018f69c64a rebaseit/rebot-telegram-bot:latest
docker tag f3018f69c64a rebaseit/rebot-telegram-bot:0.2
```

Now verify the new images:

```bash
$ docker images
REPOSITORY                          TAG                 IMAGE ID            CREATED             SIZE
rebaseit/rebot-telegram-bot         0.2                 f3018f69c64a        6 minutes ago       804 MB
rebaseit/rebot-telegram-bot         latest              f3018f69c64a        6 minutes ago       804 MB
```

### Running ReBot image on OpenShift

As first step, import the image:

```bash
$ oc import-image rebot-telegram-bot:0.2 --from=docker.io/rebaseit/rebot --confirm
The import completed successfully.
                                                                                  
Name:               rebot-telegram-bot
Namespace:          rebot
Created:            Less than a second ago
Labels:             <none>
Annotations:        openshift.io/image.dockerRepositoryCheck=2018-08-18T02:19:19Z
Docker Pull Spec:   docker-registry.default.svc:5000/rebot/rebot-telegram-bot
Image Lookup:       local=false
Unique Images:      1
Tags:		    1
...

Docker Labels:	description=ReBot Telegram Bot container image
...
Environment:	PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
```

After that, a new ImageStream will be available:

```bash
$ oc get is
NAME                 DOCKER REPO                                                 TAGS      UPDATED
rebot-telegram-bot   docker-registry.default.svc:5000/rebot/rebot-telegram-bot   0.2       5 minutes ago
```

And, the last step is instantiate a new app using the image imported above:

```bash
oc new-app -f https://raw.githubusercontent.com/rebase-it/rebot/master/rebot-container-image/template/rebot-application-template-for-k8s.yaml  \ 
-p REBOT_TELEGRAM_TOKEN_ID=<TELEGRAM_TOKEN_ID> \
-p REBOT_TELEGRAM_USER_ID=<TELEGRAM_USER_ID> \
-p REBOT_TELEGRAM_CHAT_ID=<TELEGRAM_CHAT_ID> \ 
-p REBOT_TELEGRAM_LOG_LEVEL=TRACE \
-p IMAGE_STREAM_NAMESPACE=<NAMESPACE_WHERE_BOT_WILL_BE_INSTALLED>

--> Deploying template "rebot/rebot" for "https://raw.githubusercontent.com/rebase-it/rebot/master/rebot-container-image/template/rebot-application-template-for-k8s.yaml" to project rebot
...
--> Success
    Access your application via route 'rebot-rebot.severinocloud.com' 
    Run 'oc status' to view your app.
```


At this moment the images are ready to use, enjoy :)

### Did you find a bug or do you have a suggestion?
Feel free to raise a [issue](https://github.com/rebase-it/rebot/issues/new) or send a email to just@rebase.it