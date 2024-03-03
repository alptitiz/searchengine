package pgdp.searchengine.networking;

public class HTTPResponse {
	private HTTPStatus status;
	private String html;

	public void setHtml(String html) {
		this.html = html;
	}

	public HTTPResponse(String html) {

		String[] res = html.split("\r\n");
	
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
		StringBuilder result = new StringBuilder();
		boolean findHtml = false;
		boolean findEnd = false;
		for (int i = 0; i < res.length; i++) {
			String[] currentLine = res[i].split(" ");

			if (findHtml && !findEnd) {
				addWithoutSplit(res, result, i);
				this.html = result.toString();
				return;
			}
			for (int k = 0; k < currentLine.length; k++) {
				if (!findHtml && startingPoint(currentLine[k], result)) {
					findHtml = true;
					continue;
				}
				if(findHtml && endingPoint(currentLine[k], result)) {
					findEnd = true;
					continue;
				}
				
				if (findHtml && !findEnd) {
					result.append(" " + currentLine[k]);
				}
			

			}

		}

		
		this.html = result.toString();
		
		
	}

	public boolean endingPoint(String temp, StringBuilder result) {
		Boolean findHtml = false;
		
			
				if (temp.toLowerCase().contains("</html>")) {
					String end = temp;
					result.append(" ");
					for(int k= 0; k< end.length(); k++) {
						int remain2 = end.length() - k;
						if(remain2 > 6 && !end.substring(k, k+7).toLowerCase().equals("</html>")) {
							result.append(end.charAt(k));
							
						}else if(remain2 > 6 && end.substring(k, k+7).toLowerCase().equals("</html>")) {
							result.append(end.substring(k, k+7));
							findHtml = true;
							return findHtml;
						}
							
						}
					
					
					}
				


		
		return findHtml;
		}

	
	public boolean startingPoint(String temp, StringBuilder result) {
		Boolean endHtml = false;
		Boolean findHtml = false;
		if (temp.toLowerCase().contains("</html>")) {
			endHtml = true;	

		}
		if (!temp.toLowerCase().contains("<html")) {
			return false;

		}
		
		
		for (int j = 0; j < temp.length(); j++) {
			int remain = temp.length() - j;
			if (remain > 4 && temp.substring(j, j+5).toLowerCase().equals("<html")) {
				findHtml = true;	

			}

			if(endHtml && remain > 6 && temp.substring(j, j+7).toLowerCase().equals("</html>")) {
				result.append(temp.substring(j, j+7));
				findHtml = false;
				return findHtml;
			}
			if(findHtml) {
				result.append(temp.charAt(j));
			}
			
			
		}
		return findHtml;
		
		
	}
	

	public void addWithoutSplit(String[] res, StringBuilder result, int i) {
		result.append("\r\n");
		for (int k = i; k < res.length; k++) {
		
			if (res[k].toLowerCase().contains("</html>")) {
				String end = res[k];
				
				for(int j= 0; j< end.length(); j++) {
					int remain = end.length() - j;
					if(remain > 6 && !end.substring(j, j+7).toLowerCase().equals("</html>")) {
						result.append(end.charAt(j));
						
					}else if(remain > 6 && end.substring(j, j+7).toLowerCase().equals("</html>")) {
						result.append(end.substring(j, j+7));
						return;
					}
				
				
				}
				
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