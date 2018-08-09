
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

corn.gzip({
    input: "fileTestGzip.txt",
    output: "test.gzp",
    definition: {}
});
