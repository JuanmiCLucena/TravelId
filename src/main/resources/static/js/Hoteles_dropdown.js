// 1. Init Search

$(document).ready(function () {
  "use strict";

  initSearch();

  function initSearch() {
    if ($(".search_dropdown").length) {
      var dds = $(".search_dropdown");
      dds.each(function () {
        var dd = $(this);
        if (dd.find("ul > li").length) {
          var ddItems = dd.find("ul > li");
          dd.on("click", function () {
            dd.toggleClass("active");
          });
          ddItems.each(function () {
            var ddItem = $(this);
            ddItem.on("click", function () {
              dd.find("span").text(ddItem.text());
            });
          });
        }
      });
    }
  }
});

