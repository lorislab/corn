FROM lorislab/base-jdk:8 as build

COPY corn-assembly/target/*.zip /opt/

RUN cd /opt \
    && mkdir corn \
    && mv corn-assembly-*.zip /opt/corn/corn.zip \
    && cd corn \
    && unzip corn.zip \
    && rm corn.zip

FROM lorislab/base-jdk:8 

COPY --from=build /opt/corn /opt/corn

RUN cd /opt/corn && chmod +x corn

ENV PATH $PATH:/opt/corn