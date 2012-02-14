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

## Other features

* Jade, HAML (SCAML), SSP support (through Scalate)
* ZUSS support (Java alternative for LESS/SCSS)

## Usage

For information on how to _use_ Monkeyman, check the [sample blog]
(http://monkeyman.flotsam.nl/). 

But basically, it boils down to this:

* Create a `source` directory; store HAML, Jade, SSP, Markdown files
  with YAML frontmatter and other static resources in that directory.
* Create a `layout` directory; store HAML, Jade or SSP layouts in that
  directory. 
* Have a web server pick up all of the changes you are making,
  providing you live preview, by typing `monkeyman server`. 
* Build your final version in the `target` directory, by typing
  `middleman generate`. 

## Changes

0.1

2012-02-13: Add live preview support.
2012-02-14: Add LESS support.

