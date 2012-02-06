#README

##Introduction

Monkeyman is a Scala static web site generator, similar to Middleman. At this stage, it doesn't even come close to the features Middleman has to offer, but it has one major advantage for Scala programmers: it's written in Scala. 

Monkeyman was created out of unease with the existing blogging solutions. I know, that sounds weird, because there's an abundance of Blogging solutions out there, and this certainly isn't the first static web site generator. However, this tool would not have been created if there _would_ have been a solution that supports:

* Layouts in HAML (or in this case, SCAML)
* Content pages in Markdown
* Nice permalinks
* Offline editing and preview
* Scala extensions

If ever such a solution arrives, then there is a chance Monkeyman will no longer be maintained. 

##Usage

Create a new project, and place files that you want to get converted to HTML into a `source` directory. Place `layout.scaml` (or `layout.jade`, `layout.ssp`) pages in the `layout` directory. Run:

    monkeyman generate

... to build your web site. 

For Markdown pages, Monkeyman offers limited support for YAML frontmatter. Anything that you put between two `---` lines at the top of a Markdown file will be interpreted as YAML. Currently, it only extracts the `tags` and `title` property, and makes this available to your layout or other template pages. 

##Limitations

Live preview is not implemented yet. Once it's there, you will be able to get it by typing `monkeyman server`. 


