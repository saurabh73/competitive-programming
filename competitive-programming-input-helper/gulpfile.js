"use strict";

// Load plugins
const gulp = require("gulp");
const babel = require("gulp-babel");
const gulpCopy = require("gulp-copy");
const gulpInline = require("gulp-inline");
const uglify = require("gulp-uglify");
const minifyCss = require("gulp-minify-css");
const clean = require("gulp-clean");

// Clean dist
const cleanDist = () => {
  return gulp.src("./dist", { read: false, allowEmpty: true }).pipe(clean({force: true}));
};

// Clean temp
const cleanTemp = () => {
  return gulp.src("./temp", { read: false, allowEmpty: true}).pipe(clean({force: true}));
};

const distCopy = () => {
  return gulp
    .src(["./dist/index.html"], { allowEmpty: true})
    .pipe(gulpCopy("./../src/main/resources/public", {prefix : 1}));
};

const tempCopy = () => {
  return gulp
    .src(["./*.html", "./css/*.css", "./js/*.js"], { allowEmpty: true})
    .pipe(gulpCopy("./temp/"));
};

const babelTransform = () => {
  return gulp
    .src("./temp/js/script.js")
    .pipe(
      babel({
        presets: ["@babel/env"],
      })
    )
    .pipe(gulp.dest("./temp/js/"));
};

const inlineHtml = () => {
  return gulp.src("./temp/index.html")
    .pipe(
      gulpInline({
        base: "./",
        js: uglify,
        css: [minifyCss],
        disabledTypes: ["svg", "img"],
        ignore: [],
      })
    )
    .pipe(gulp.dest("dist/"));
};

// Export tasks
const cleanTask =  gulp.series(cleanDist, cleanTemp);
exports.clean = cleanTask;
const buildTask = gulp.series(cleanTask, tempCopy,babelTransform,inlineHtml,cleanTemp);
exports.build = buildTask;
exports.copy = gulp.series(buildTask, distCopy);
exports.default = buildTask;


