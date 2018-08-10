
/* global corn */

corn.println("Create file");

var data = [];

for (i=0; i<10; i++) {
    data.push("line" + i);
}
data.push("");

corn.file({
    file: "fileTestGzip.txt",
    data: data,
    definition: {
        version: "1"
    }
});

corn.gzip("fileTestGzip.txt","test.gzp");

var data2 = [];
data2.push("AÐA12");
data2.push("AÐA13");
data2.push("ABA14");
data2.push("");

corn.file({
    file: "fileTestGzip2.txt",
    data: data2,
    definition: {
        version: "1",
        charset: "ISO-8859-1"
    }
});

corn.gzip("fileTestGzip2.txt","test2.gzp");