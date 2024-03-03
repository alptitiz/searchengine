package pgdp.searchengine.networking;

public class HTTPResponse {
	private HTTPStatus status;
	private String html;

	public void setHtml(String html) {
		this.html = html;
	}

	public HTTPResponse(String html) {

		String[] res = html.split("\r\n");
		StringBuilder result = new StringBuilder();
		String[] firstLine = res[0].split(" ");
		for (int i = 0; i < firstLine.length; i++) {
			if (firstLine[i].equals("200")) {
				this.status = HTTPStatus.getByCode(200);
			}

			if (firstLine[i].equals("400")) {
				this.status = HTTPStatus.getByCode(400);
			}

			if (firstLine[i].equals("403")) {
				this.status = HTTPStatus.getByCode(403);
			}

			if (firstLine[i].equals("404")) {
				this.status = HTTPStatus.getByCode(404);
			}

			if (firstLine[i].equals("405")) {
				this.status = HTTPStatus.getByCode(405);
			}
			if (firstLine[i].equals("408")) {
				this.status = HTTPStatus.getByCode(408);
			}
		}

		boolean findHtml = false;
		for (int i = 0; i < res.length; i++) {
			String[] currentLine = res[i].split(" ");

			if (findHtml) {
				addWithoutSplit(res, result, i);
				this.html = result.toString();
				return;
			}
			for (int k = 0; k < currentLine.length; k++) {
				if (findHtml) {
					result.append(" " + currentLine[k]);
				}
				if (startingPoint(currentLine[k], result)) {
					findHtml = true;

				}

			}

		}

	}

	public boolean startingPoint(String temp, StringBuilder result) {
		Boolean findHtml = false;
		for (int j = 0; j < temp.length(); j++) {
			int remain = temp.length() - j;

			if (remain > 5 && Character.toLowerCase(temp.charAt(j)) == '<'
					&& Character.toLowerCase(temp.charAt(j + 1)) == 'h'
					&& Character.toLowerCase(temp.charAt(j + 2)) == 't'
					&& Character.toLowerCase(temp.charAt(j + 3)) == 'm'
					&& Character.toLowerCase(temp.charAt(j + 4)) == 'l'
					&& Character.toLowerCase(temp.charAt(j + 5)) == '>') {

				findHtml = true;
				result.append(temp.substring(j));
				return findHtml;

			}

		}

		return findHtml;
	}

	public void addWithoutSplit(String[] res, StringBuilder result, int i) {
		result.append("\r\n");
		for (int k = i; k < res.length; k++) {
			if (k == res.length - 1) {
				result.append(res[k]);
				return;
			}

			result.append(res[k] + "\r\n");
		}

	}

	public HTTPStatus getStatus() {
		return status;
	}

	public String getHtml() {
		return html;
	}
}