/* global org, engine, java */

(function(){
    "use strict";
    var result = {
        generator : new org.lorislab.corn.beans.GeneratorBean(engine)
    };
    result.csv = function(data) {
        return this.generator.csv(data);
    };
    result.xml = function(data) {
        return this.generator.xml(data);
    };    
    result.currentDateFormat = function(format) {
        return this.generator.currentDateFormat(format);
    };
    result.currentDate = function() {
        return this.generator.currentDate();
    };    
    result.dateFormat = function(date, format) {
        return this.generator.dateFormat(date, format);
    };    
    result.uuidRandom = function() {
        return this.generator.uuidRandom();
    };
    result.uuidRandom = function(length) {
        return this.generator.uuidRandom(length);
    };
    return result;
}).call(this);

//(function () {
//    return {
//        generator: new org.lorislab.corn.beans.GeneratorBean(engine),
//        xml: function (data) {
//            return this.generator.xml(data);
//        },
//        csv: function (data) {
//            return this.generator.csv(data);
//        }
//    };
//})();
