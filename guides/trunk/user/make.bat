rem LaTeX Vorlauf
latex user.tex
latex user.tex

rem Literaturverzeichnis erzeugen
bibtex user

rem Stichwortverzeichnis erzeugen
makeindex -s index_style.lst user

rem Glossar erzeugen
makeindex -s user.ist -t user.glg -o user.gls user.glo

rem DVI-Dokument erzeugen, Glossar einbinden und Literaturverzeichnis danach nochmal aktualisieren
latex user.tex
latex user.tex

rem PDF-Dokument erzeugen
pdflatex user.tex