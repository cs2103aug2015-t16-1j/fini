package fini.main.util;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ModsLoader {
	public ModsLoader(File modsFile) {
		assert modsFile.exists();
		try {
			Document doc = Jsoup.parse(modsFile, "UTF-8");
			Elements lessons = doc.getElementsByClass("lesson");
			for (Element lesson : lessons) {
				System.out.println(lesson.text());
				System.out.println(lesson.parent().className());
				System.out.println(lesson.parent().nextElementSibling().className());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
