# README

## Introduction

Monkeyman is a Scala static web site generator, similar to
Middleman. At this stage, it doesn't even come close to the features
Middleman has to offer, but it has one major advantage for Scala
programmers: it's written in Scala.

Monkeyman was created out of unease with the existing blogging
solutions. I know, that sounds weird, because there's an abundance of
Blogging solutions out there, and this certainly isn't the first
static web site generator. However, this tool would not have been
created if there _would_ have been a solution that supports:

* Layouts in HAML (or in this case, SCAML)
* Content pages in Markdown
* Nice permalinks
* Offline editing and preview
* Scala extensions

If ever such a solution arrives, then there is a chance Monkeyman will
no longer be maintained.

## Features

* Jade, HAML (SCAML), Mustache, SSP for layouts
* LESS and ZUSS to CSS transformation
* Live preview (web server running on port 4567)
* Live updates (monitoring file system changes)
* Static web site generation
* Content management through tags

## Usage

For information on how to _use_ Monkeyman, check the [sample blog]
(http://monkeyman.flotsam.nl/), or watch
[the video](http://www.youtube.com/watch?v=9giYvVGIi0U).

But basically, it boils down to this:

* Create a `source` directory; store HAML, Jade, SSP, Markdown,
  Mustache files with YAML frontmatter and other static resources in
  that directory.
* Create a `layout` directory; store HAML, Jade or SSP layouts in that
  directory. 
* Have a web server pick up all of the changes you are making,
  providing you live preview, by typing `monkeyman server`. 
* Build your final version in the `target` directory, by typing
  `monkeyman generate`. 

## Downloads

You can download releases [here](https://github.com/wspringer/monkeyman/wiki/Releases).

## Installation

Download the monkeyman jar from the Downloads section. Start it with
`java -jar monkeyman.jar`, or create a script for it, as I did:

    #!/bin/sh
    
    java -jar ~/workspace/monkeyman/target/monkeyman.jar "$@"

## Changes

0.3 Gibbon

*Breaking changes:*

* The general pattern now is to move *.html.scaml into *.html files, *.js.coffee files into *.js files, and so on. In line with Middleman's approach.

*Other changes:*

* 2012-03-13: Genuine YAML support (decent YAML parser used now).
* 2012-03-13: Added summary and subtitle for every resource.
* 2012-12-21: Add switch to switch directory browsing on/off.
* 2012-12-21: Fix for allResources not getting updated when new files appear.
* 2013-04-18: Added coffeescript transpiler support.
* 2013-04-18: Added image resizing support.

0.2

* 2012-02-20: Default site template (no need to start creating a layout)
* 2012-02-18: File system monitoring (detect changes and update content when required)

0.1

* 2012-02-14: Add LESS support.
* 2012-02-13: Add live preview support.



