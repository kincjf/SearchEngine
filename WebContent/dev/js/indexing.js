// indexing 결과에 대한 UI

var addIndexResultList = function(result) {
	// whole search data container
	var listContainer = $("<ul></ui>").attr({
		"data-role" : "listview",
		"data-inset" : "true",
		"data-divider-theme" : "b"
	});
	// title data
	var titleView = $("<li></li>").attr({
		'data-role' : "list-divider",
		'role' : "heading"
	}).html("북마크 전송 결과");

	// totalBookmarkSize data
	var bookmarkSizeView = $("<li></li>").attr({
		'data-icon' : "refresh"
	})
	var bookmarkSizeInnerView = $("<a></a>").html("등록된 북마크 URL 갯수   -  " + result.totalBookmarkSize);
	bookmarkSizeView.append(bookmarkSizeInnerView);

	// totalURLListSize data
	var urlListSizeView = $("<li></li>").attr({
		'data-icon' : 'refresh'
	});
	var urlListSizeInnerView = $("<a></a>").attr({
		href : ""
	}).html("총 등록된 URL 갯수   -  " + result.totalURLListSize);
	urlListSizeView.append(urlListSizeInnerView);

	// assignedIdxNum:
	var assignedIdxNumView = $("<li></li>").attr({
		'data-icon' : "refresh"
	})
	var assignedIdxNumInnerView = $("<a></a>").attr({
		href : ""
	}).html("검색 시스템에 추가된 URL수   -  " + result.assignedIdxNum);
	assignedIdxNumView.append(assignedIdxNumInnerView);

	// totalAssignedIdxNum
	var totalAssignedIdxView = $("<li></li>").attr({
		'data-icon' : "refresh"
	})
	var totalAssignedIdxInnerView = $("<a></a>").attr({
		href : ""
	}).html("기여도(총 등록한 모든 url의 수)   -  " + result.totalAssignedIdxNum);
	totalAssignedIdxView.append(totalAssignedIdxInnerView);
	
	listContainer.append(titleView, bookmarkSizeView, assignedIdxNumView, totalAssignedIdxView, urlListSizeView);

	$(".messageContainer").append(listContainer);
}

