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
2019-04-12 15:29:20,628 cekit        DEBUG    Running version 2.2.7
2019-04-12 15:29:20,630 cekit        DEBUG    Removing dirty directory: 'target/image/modules'
2019-04-12 15:29:20,630 cekit        DEBUG    Removing dirty directory: 'target/repo'
2019-04-12 15:29:20,639 cekit        INFO     Generating files for docker engine.
2019-04-12 15:29:20,639 cekit        DEBUG    Loading descriptor from path 'image.yaml'.
2019-04-12 15:29:20,668 cekit        INFO     Using overrides file from 'dev-overrides.yaml'.
2019-04-12 15:29:20,668 cekit        DEBUG    Loading descriptor from path 'dev-overrides.yaml'.
2019-04-12 15:29:20,677 cekit        INFO     Initializing image descriptor...
2019-04-12 15:29:20,677 cekit        DEBUG    Retrieving module repositories for 'rebaseit/rebot'
2019-04-12 15:29:20,678 cekit        INFO     Preparing resource 'modules'
....
2019-04-12 15:29:21,358 cekit        INFO     Using Docker builder to build the image.
2019-04-12 15:29:21,358 cekit        DEBUG    Building image with tags: 'rebaseit/rebot:1.0-SNAPSHOT', 'rebaseit/rebot:latest'
...
2019-04-12 15:30:00,479 cekit        INFO     Docker: Successfully built 18b191d8a0bd
2019-04-12 15:30:00,595 cekit        INFO     Image built and available under following tags: rebaseit/rebot:1.0-SNAPSHOT, rebaseit/rebot:latest
2019-04-12 15:30:00,595 cekit        INFO     Finished!
```

As result, we have a new image *quay.io/rebase-it/rebot:* with the **1.0-SNAPSHOT** and **latest** tags.


You can check it by running the following command:

```bash
$ docker images
REPOSITORY          T        AG                 IMAGE ID            CREATED             SIZE
quay.io/rebase-it/rebot      1.0-SNAPSHOT        18b191d8a0bd        2 minutes ago       475 MB
quay.io/rebase-it/rebot      latest              18b191d8a0bd        2 minutes ago       475 MB
```

You can now run the image:

```bash
$ docker run -it --env REBOT_TELEGRAM_TOKEN_ID=YOUR_TOKEN_ID --env REBOT_TELEGRAM_USER_ID=YOUR_BOT_USER_ID \ 
--env REBOT_TELEGRAM_CHAT_ID=YOUR_CHAT_ID --env REBOT_TELEGRAM_LOG_LEVEL=trace  quay.io/rebase-it/rebot:latest

Bot correctly configured.
Running ReBot image with the following configurations:
REBOT_TELEGRAM_TOKEN_ID: OMITTED
REBOT_TELEGRAM_USER_ID: OMITTED
..
REBOT_TELEGRAM_LOG_LEVEL: FINE
...
```

If you do have a Yahoo developer account which grants you access to the Yahoo's weather api you can also provide it with the following envs:

```BASH
- REBOT_TELEGRAM_WEATHER_APP_ID
- REBOT_TELEGRAM_WEATHER_CONSUMER_KEY
- REBOT_TELEGRAM_WEATHER_CONSUMER_SECRET
```

For more information about how to create an Yahoo app, please refer to this [link](https://developer.yahoo.com/weather/).
Those parameters are required for the weather plugin work.


### Squashing the image

If you check the image size you will notice that its size is **1.19 GB**.

It is recommended squash it before publish the image, for example, in the docker hub.

To squash the image follow the next steps:


Get the image id

```bash
$ docker images
REPOSITORY                   TAG                 IMAGE ID            CREATED             SIZE
quay.io/rebase-it/rebot      1.0-SNAPSHOT        de88568e6ba8        2 minutes ago       475 MB
quay.io/rebase-it/rebot      latest              de88568e6ba8        2 minutes ago       475 MB
```

In this case the image ID is **18b191d8a0bd**, with the id in hands we can now squash it:


```bash
$ docker-squash de88568e6ba8 --tag=quay.io/rebase-it/rebot:1.0-SNAPSHOT --from e05a9ff73944
...
2019-04-12 15:57:21,748 root         INFO     Original image size: 456.34 MB
2019-04-12 15:57:21,748 root         INFO     Squashed image size: 381.25 MB
2019-04-12 15:57:21,748 root         INFO     Image size decreased by 16.45 %
2019-04-12 15:57:21,748 root         INFO     New squashed image ID is 8eeba7995f3f718f74c8047f03d07f1266e312d8e00eecd24b56e517409e92d1
2019-04-12 15:57:22,888 root         INFO     Image registered in Docker daemon as quay.io/rebase-it/rebot:1.0-SNAPSHOT
2019-04-12 15:57:22,938 root         INFO     Done
```

The --from parameter is dynamic that points to the last base image layer.

Note that a new image was generated and its size decreased from **456.34 MB** to **381.25 MB**.

Now verify the new size:

```bash
$ docker images
REPOSITORY                   TAG                 IMAGE ID            CREATED             SIZE
quay.io/rebase-it/rebot      1.0-SNAPSHOT        8eeba7995f3f        2 minutes ago       396 MB
```

### Running ReBot image on OpenShift

As first step, import the image:

```bash
$ oc new-project rebot
$ oc tag --source=docker quay.io/rebase-it/rebot:1.0-SNAPSHOT rebot/rebot:1.0-SNAPSHOT --reference-policy=local 
Tag rebot:1.0-SNAPSHOT set to quay.io/rebase-it/rebot:1.0-SNAPSHOT.
```

Then we can verify the image stream:
```bash
$ oc describe is rebot
Name:			rebot
Namespace:		rebot
Created:		6 minutes ago
Labels:			<none>
Annotations:		openshift.io/image.dockerRepositoryCheck=2019-04-12T19:08:17Z
Docker Pull Spec:	docker-registry.default.svc:5000/toto/rebot
Image Lookup:		local=false
Unique Images:		1
Tags:			1

1.0-SNAPSHOT
  tagged from quay.io/rebase-it/rebot:1.0-SNAPSHOT
    prefer registry pullthrough when referencing this tag

  * quay.io/rebase-it/rebot@sha256:7d5c0d68d3ecc905ce5e7392ce1a56b90548ee9e014456ae83cb0c0db795352c
      13 seconds ago

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
-p REBOT_TELEGRAM_LOG_LEVEL=TRACE \
-p IMAGE_STREAM_NAMESPACE=<NAMESPACE_WHERE_BOT_WILL_BE_INSTALLED>
```


At this moment the images are ready to use, enjoy :)

### Did you find a bug or do you have a suggestion?
Feel free to raise a [issue](https://github.com/rebase-it/rebot/issues/new) or send a email to just@rebase.it