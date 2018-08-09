
/* global corn */

corn.println("Create file");

var data = [];

for (i=0; i<10; i++) {
    data.push("line" + i);
}
data.push("");

corn.mkdir("zip");
corn.mkdir("zip/subdir");

corn.file({
    file: "zip/fileTestZip1.txt",
    data: data,
    definition: {
        version: "1"
    }
});
corn.file({
    file: "zip/fileTestZip2.txt",
    data: data,
    definition: {
        version: "1"
    }
});
corn.file({
    file: "zip/subdir/fileTestZip2.txt",
    data: data,
    definition: {
        version: "1"
    }
});

corn.zip({
    input: "zip/fileTestZip1.txt",
    output: "fileTestZip.zip"
});

corn.zip({
    input: "zip",
    output: "directory.zip"
});
