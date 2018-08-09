
/* global corn */

corn.println("Create file");

var data = [];

for (i=0; i<10; i++) {
    data.push("line" + i);
}
data.push("");

corn.file({
    file: "fileTest.txt",
    data: data,
    definition: {
        version: "1"
    }
});

corn.copy("fileTest.txt", "fileTestCopy.txt");
corn.copy("fileTest.txt", "toDelete.txt");
corn.delete("toDelete.txt");