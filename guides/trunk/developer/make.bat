rem LaTeX Vorlauf
latex developer.tex
latex developer.tex

rem Literaturverzeichnis erzeugen
bibtex developer

rem Stichwortverzeichnis erzeugen
makeindex -s index_style.lst developer

rem Glossar erzeugen
makeindex -s developer.ist -t developer.glg -o developer.gls developer.glo

rem DVI-Dokument erzeugen, Glossar einbinden und Literaturverzeichnis danach nochmal aktualisieren
latex developer.tex
latex developer.tex

rem PDF-Dokument erzeugen
pdflatex developer.tex
