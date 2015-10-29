
var compareView;

function ImageCanvas() {
	canvas 		= undefined;
	element 	= undefined;
	img 		= undefined;
	resetScale	= undefined;
	currentZoom = undefined;
	panned      = true;

	ImageCanvas.prototype.finalize = function() {
		
		this.canvas.dispose();
		this.canvas 		= undefined;
		this.element 		= undefined;
		this.img 			= undefined;
		this.resetScale  	= undefined;
		this.currentZoom 	= undefined;
		this.panned 		= false;
	}

	ImageCanvas.prototype.init = function(element, src) {
		this.canvas  = new fabric.Canvas(element);
		this.canvas.containerClass = "default_canvas_style";
		this.canvas.selection = false;
		this.element = $('#'+element);
		this.currentZoom = undefined;
		this.canvas.interactive = true;
		var that = this;
		fabric.Image.fromURL(src, function(oImg) {
			that.img = oImg;
			that.showImage();
		});
	}

	ImageCanvas.prototype.showImage = function() {
		this.img.hasControls = false;
		this.img.alignX = "min";
		this.img.alignY = "min";
		this.img.meetOrSlice = "slice";
		this.img.centeredScaling = true;
		this.img.originX = "left";
		this.img.originY = "top";
		this.img.borderColor = "#818181";
		this.img.setShadow({color: "#818181", blur: 20, offsetX: 10, offsetY:10});
		this.canvas.add(this.img);
		this.adjustDimensions();
		this.center();
	}

	ImageCanvas.prototype.adjustDimensions = function() {
		if (this.img == undefined)
			return;
		var scaleH = (this.img.height + 20) /this.element.parent().parent().height();
		var scaleW = (this.img.width + 10) /this.element.parent().parent().width();

		if (scaleH > scaleW) {
			this.resetScale = 1 / scaleH;
		} else {
			this.resetScale = 1 / scaleW;
		}
		
		this.element.width(this.element.parent().parent().width());
		this.element.height(this.element.parent().parent().height());
		
		this.canvas.setHeight(this.element.height());
		this.canvas.setWidth(this.element.width());	
		if (this.currentZoom == undefined) {
			this.currentZoom = this.resetScale;
		}
		this.canvas.setZoom(this.currentZoom);	
	}

	ImageCanvas.prototype.center = function() {

		var scaledImgWidth = this.img.width * this.canvas.getZoom();
		var left = (this.canvas.width - scaledImgWidth) / 2;
		this.img.left = left / this.canvas.getZoom();
		
		var scaledImgHeight = this.img.height * this.canvas.getZoom();
		var top = (this.canvas.height - scaledImgHeight) / 2;
		if (top > 1) {
			this.img.top = top / this.canvas.getZoom();
		}
		this.canvas.renderAll();

	}

	ImageCanvas.prototype.pan = function(relativeTo) {
		this.img.left = relativeTo.img.left;
		this.img.top = relativeTo.img.top;
		this.canvas.renderAll();
	}

	ImageCanvas.prototype.zoomIn = function() {
		if (this.currentZoom * 1.1 > 1.75)
			return;
		this.currentZoom = this.currentZoom * 1.1;
		this.canvas.setZoom(this.currentZoom);
		if (!this.panned) 
			this.center();
	}

	ImageCanvas.prototype.zoomOut = function() {
		if (this.currentZoom * 1.1 < resetScale)
			return;
		this.currentZoom = this.currentZoom * 0.9;
		this.canvas.setZoom(this.currentZoom);
		if (!this.panned)
			this.center();
	}

	ImageCanvas.prototype.zoomActual = function() {
		this.panned = false;
		this.currentZoom = 1;
		this.canvas.setZoom(this.currentZoom);
	}

	ImageCanvas.prototype.reset = function() {
	    this.panned = false;
		this.currentZoom = this.resetScale;
		this.canvas.setZoom(this.currentZoom);
		this.center();
	}
}

function CompareImageView() {

	base 			 = undefined;
	compare 		 = undefined;

	CompareImageView.prototype.finalize = function () {
		if (this.base != undefined)
			this.base.finalize();
		if (this.compare != undefined)
			this.compare.finalize();
		this.base    = undefined;
		this.compare = undefined;
	}
	// Effectively the constructor, should be called every time.
	CompareImageView.prototype.initView = function(baseImageCanvas, baseImageSrc, compareImageCanvas, compareImageSrc) {
		this.base 	 = new ImageCanvas();
		this.compare = new ImageCanvas();
		
		this.base.init(baseImageCanvas, baseImageSrc);
		this.compare.init(compareImageCanvas, compareImageSrc);
		
		var that = this;
		this.base.canvas.on('object:moving', function(eventObj) {
			that.pan(eventObj);
		});

		this.compare.canvas.on('object:moving', function(eventObj) {
			that.pan(eventObj);
		});
	}

	// Render, sets the scaling and make sures the canvas is sized the right way
	CompareImageView.prototype.render = function (callback) {
		if (this.baseImageWidth != undefined) {
			this._render(this.baseCanvasElem,this.baseCanvas);
			this._render(this.compareCanvasElem, this.compareCanvas);
			if (callback != undefined)
				callback();
		}
	}

	CompareImageView.prototype.pan = function(eventObj) {
		console.log(eventObj);
		if (eventObj.target != undefined && this.base != undefined && this.compare != undefined) {
			var moved;
			var toBeMoved;

			if (eventObj.target == this.base.img || eventObj.target == this.base.canvas) {
				moved = this.base;
				toBeMoved = this.compare;
			} else if (eventObj.target == this.compare.img || eventObj.target == this.compare.canvas) {
				moved = this.compare;
				toBeMoved = this.base;
			}

			if (toBeMoved != undefined) {
			    moved.panned = true
			    toBeMoved.panned = true
				window.requestAnimationFrame( function () {
					toBeMoved.pan(moved);
				});
			} else {
				console.log("toBeMoved is null", eventObj.target, eventObj.target.type);
			}
		}
	}

	CompareImageView.prototype.adjustDimensions = function() {
		if (this.showingDiffImage) {
			if (this.diff != undefined)
				this.diff.adjustDimensions();
		} else {
			if (this.base != undefined) {
				this.base.adjustDimensions();
				this.compare.adjustDimensions();
			}
		}
	}

	CompareImageView.prototype.reset = function() {
		if (this.showingDiffImage) {
			this.diff.reset();
		} else {
			this.base.reset();
			this.compare.reset();
		}
	}

	CompareImageView.prototype.zoomIn = function() {
		if (this.showingDiffImage) {
			this.diff.zoomIn();
		} else {
			this.base.zoomIn();
			this.compare.zoomIn();
		}
	}

	CompareImageView.prototype.zoomOut = function() {
		if (this.showingDiffImage) {
			this.diff.zoomOut();
		} else {
			this.base.zoomOut();
			this.compare.zoomOut();
		}
	}

	CompareImageView.prototype.zoomActual = function() {
		if (this.showingDiffImage) {
			this.diff.zoomActual();
		} else {
			this.base.zoomActual();
			this.compare.zoomActual();
		}
	}
}

function showCompareImages(srcOne, srcTwo, failureCount, failurePercent, result) {
	 $("#mainHeader").hide();
	 $("#resultsDiv").hide();

	 $("#stepHeader").show();
	 $("#overlay_view").fadeIn("slow");

	 $("#stepNoOfFailuresId").text(failureCount);
	 $("#stepFailurePercentId").text(failurePercent);
	 $("#stepResultId").text(result);

	 compareView = new CompareImageView();
	 compareView.showingDiffImage = false;
	$("#compareRow").show();
	$("#footerToolbar").show();
	fitToWindow();

	var canvasHTML = "<canvas class='canvasStyle' id='c1'>";
	$("#baseTD").append(canvasHTML);

	canvasHTML = "<canvas class=\"canvasStyle\" id=\"c2\">";
	$("#compareTD").append(canvasHTML);

	compareView.initView('c1',srcOne,'c2',srcTwo);	
}

function closeCompareImages() {
		$("#stepHeader").hide();
		$("#overlay_view").hide();
		$("#footerToolbar").hide();
		$("#mainHeader").show();
	 	$("#resultsDiv").show();

	 	compareView.finalize();
	 	$("#c1").parent().remove();
	 	$("#c2").parent().remove();
	 	compareView = undefined;
}

function buildTestResults() {

	var resultsDiv = $("#resultsDiv");
	var noResults = testResultsJSON.testSuiteResult.suiteResults.length;

	$("#startTime").html(testResultsJSON.testSuiteResult.startTime);
	$("#endTime").html(testResultsJSON.testSuiteResult.endTime);
	$("#result").html(testResultsJSON.testSuiteResult.result);
	
	var divHTML = "<table id='resultsTable'>";
	
	for (var index=0; index < noResults; index++) {
		var suiteResults = testResultsJSON.testSuiteResult.suiteResults[index];
		// Show the file name if there are more than one file
		var suiteName = (noResults > 1 ? suiteResults.file : "&nbsp;");

		divHTML = divHTML + "<tr><td colspan=5 class='headerTD'>" + suiteName + "</td>";
		var cssClass = "headerResultFailTD";
		if (suiteResults.pass.toUpperCase() == "pass".toUpperCase()) {
			cssClass = "headerResultPassTD";
		}
		divHTML = divHTML +  "<td class='" + cssClass + "'>" + suiteResults.pass + '</td></tr>';
		divHTML = divHTML + buildStepResults(suiteResults.fileResults,index);
		divHTML = divHTML + "<tr><td class='noStyleTD'>&nbsp;</td></tr>";
	}

	divHTML = divHTML + "</table>";
	
	resultsDiv.append(divHTML);
}

function buildStepResults(stepResults, suiteIndex) {
	
	var tableHTML = "";
	for (var index=0; index < stepResults.length; index++) {
		var stepResult = stepResults[index].stepResult;
		if (stepResult.pass.toUpperCase() == "Pass".toUpperCase()) {
			tableHTML = tableHTML + "<tr class='xyz'>";
		} else {
			tableHTML = tableHTML + "<tr>";
		}
		tableHTML = tableHTML + "<td colspan=4 class='descTD'>" + stepResult.description + "</td>";
		var message;
		if (stepResult.pass.toUpperCase() == "Pass".toUpperCase()) {
			tableHTML = tableHTML + "<td class='passTD'>Pass</td>";
			message = "View Images";
		} else {
			tableHTML = tableHTML + "<td class='failTD'>" + stepResult.failureCount + " difference(s) causing " + stepResult.diffPercent + " mismatch</td>";
			message = "View Differences";
		}

		tableHTML = tableHTML + "<td><a onclick=\"";
		tableHTML = tableHTML + "showCompareImages('" + stepResult.baseImage + "','" + stepResult.compareImage + "','";
		tableHTML = tableHTML + String(stepResult.failureCount) +  "','" + stepResult.diffPercent + "','" + stepResult.pass +  "')\">";
		tableHTML = tableHTML + message  + "</a></td>";

		tableHTML = tableHTML + "</tr>";
	}
	
	return tableHTML;
}

function fitToWindow() {
	
	/*compareView.render(function () {
			var maxHeight = 0;
			if ($("#diffimg").is(":visible")) {
				maxHeight = compareView.diffCanvas.getHeight();
			} else {
				maxHeight = compareView.baseCanvas.getHeight();
				if (maxHeight < compareView.compareCanvas.getHeight()) {
					maxHeight = compareView.compareCanvas.getHeight();
				}
				maxHeight = compareView.baseCanvas.getHeight();
			}
			
			$("#overlay_view").height(maxHeight+10);
			});
	*/
	if (compareView != undefined)
		compareView.adjustDimensions();
}

$(document).ready(function () {

	 $("#overlay_view").hide();
	 $("#stepHeader").hide();
	 $("#footerToolbar").hide();
	 buildTestResults();
});

$(window).resize(function() {
    console.log("Resizing...");
	if($("#overlay_view").is(":visible")) {
		//fitToWindow();
		compareView.adjustDimensions();
	};
});

var testResultsJSON = {
	"testSuiteResult": {
	    "result": "Pass",
	    "startTime": "10/08 09:25",
	    "endTime": "10/10 10:25",
	    "suiteResults": [
	        	{
	                "file": "__default__",
	                "pass": "Pass",
	                "fileResults": [
	                    {
	                        "stepResult": {
	                            "description": "My HCL Home Page",
	                            "pass": "Fail",
	                            "baseImage": "file:///D:/temp/uiverify/results/20151002-1645/images/base_lego_home.png",
	                            "compareImage": "file:///D:/temp/uiverify/results/20151002-1645/images/compare_lego_home.png",
	                            "diffImage": "file:///D:/temp/uiverify/results/20151002-1645/images/diff_lego_home.png",
	                            "failureCount": "4",
	                            "diffPercent": "4.9%",
	                             "diffRects": [
	                                {
	                                    "x": "584",
	                                    "y": "184",
	                                    "width": "1",
	                                    "height": "16"
	                                },
	                                {
	                                    "x": "701",
	                                    "y": "370",
	                                    "width": "173",
	                                    "height": "10"
	                                },
	                                {
	                                    "x": "1351",
	                                    "y": "578",
	                                    "width": "12",
	                                    "height": "56"
	                                },
	                                {
	                                    "x": "0",
	                                    "y": "641",
	                                    "width": "1365",
	                                    "height": "29"
	                                }
	                            ]
	                        }
	                    },
	                    {
	                        "stepResult": {
	                            "description": "My HCL Login Page",
	                            "pass": "Pass",
	                             "baseImage": "file:///D:/temp/uiverify/results/20150828-1952/images/base_myhcl_home.png",
	                            "compareImage": "file:///D:/temp/uiverify/results/20150828-1952/images/compare_myhcl_home.png",
	                            "diffImage": "file:///D:/temp/uiverify/results/20150828-1952/images/diff_myhcl_home.png",
	                            "failureCount": "2",
	                            "diffPercent": "2.4%",
	                            "diffRects": [
	                                {
	                                    "x": "562",
	                                    "y": "162",
	                                    "width": "45",
	                                    "height": "59"
	                                },
	                                {
	                                    "x": "551",
	                                    "y": "348",
	                                    "width": "345",
	                                    "height": "54"
	                                }
	                            ]
	                        }
	                    }
	                ]
	            },
	            {
	                "file": "file2",
	                "pass": "Pass",
	                "fileResults": [
	                    {
	                        "stepResult": {
	                            "description": "my desc",
	                            "pass": "Fail",
	                            "baseImage": "file:///D:/temp/uiverify/results/20151002-1645/images/base_lego_home.png",
	                            "compareImage": "file:///D:/temp/uiverify/results/20151002-1645/images/compare_lego_home.png",
	                            "diffImage": "file:///D:/temp/uiverify/results/20151002-1645/images/diff_lego_home.png",
	                            "failureCount": "2",
	                            "diffPercent": "2.8%",
	                            "diffRects": [
	                                {
	                                    "x": "562",
	                                    "y": "162",
	                                    "width": "45",
	                                    "height": "59"
	                                },
	                                {
	                                    "x": "551",
	                                    "y": "348",
	                                    "width": "345",
	                                    "height": "54"
	                                }
	                            ]
	                        }
	                    },
	                    {
	                        "stepResult": {
	                            "description": "my descx",
	                            "pass": "Pass",
	                            "baseImage": "file:///D:/temp/uiverify/results/20151002-1645/images/base_lego_home.png",
	                            "compareImage": "file:///D:/temp/uiverify/results/20151002-1645/images/compare_lego_home.png",
	                            "diffImage": "file:///D:/temp/uiverify/results/20151002-1645/images/diff_lego_home.png",
	                            "failureCount": "2",
	                            "diffPercent": "2.4%",
	                            "diffRects": [
	                                {
	                                    "x": "562",
	                                    "y": "162",
	                                    "width": "45",
	                                    "height": "59"
	                                },
	                                {
	                                    "x": "551",
	                                    "y": "348",
	                                    "width": "345",
	                                    "height": "54"
	                                }
	                            ]
	                        }
	                    }
	                ]
	            }
	        ]
	    }
	}
