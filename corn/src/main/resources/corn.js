/* global org, engine, java, corn_config */

(function(){
    "use strict";
    var result = {
        generator : new org.lorislab.corn.beans.GeneratorBean(corn_config.target)
    };
    result.csv = function(data) {
        return this.generator.csv(data);
    };
    result.xml = function(data) {
        return this.generator.xml(data);
    };    
    result.date = function(format) {
        return this.generator.currentDateFormat(format);
    };
    result.date = function() {
        return this.generator.currentDate();
    };    
    result.date = function(date, format) {
        return this.generator.dateFormat(date, format);
    };    
    result.uuid = function() {
        return this.generator.uuidRandom();
    };
    result.uuid = function(length) {
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
