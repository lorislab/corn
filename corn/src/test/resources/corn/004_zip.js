
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
    file: "fileTestZip0.txt",
    data: data,
    definition: {
        version: "1"
    }
});
corn.zip("fileTestZip0.txt","fileTestZip0.zip");

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

corn.zip("zip/fileTestZip1.txt", "fileTestZip.zip");

corn.zip("zip","directory.zip");
