 class ELNLoaderProgress {

 	constructor(args = {}) {
 		this.init(args);
 	}

 	init(args) {
 		this.progress = args.progress || 0; //初始进度
 		this.currentProgress = 0;
 		this.domParent = args.domParent || document.body; //父dom
 		this.title = args.title || '数据加载中...'; //进度标题
 		this.maxTime = args.maxTime || 100000; //进度加载完的最大时间，由于界面加载时间未定，故提供一个最大时间控制
 		this.render();
 	}

 	render() {
 		let mask = this.mask = document.createElement('div');
 		mask.className = 'eln-progress-mask';
 		const wrapper = this.wrapper = document.createElement('div');
 		wrapper.className = 'eln-progress-wrapper';
 		mask.appendChild(wrapper)
 		const title = document.createElement('div');
 		title.className = 'eln-progress-title';
 		title.innerText = this.title;
 		wrapper.appendChild(title);
 		const progressContent = document.createElement('div');
 		progressContent.className = 'eln-progress-content';
 		wrapper.appendChild(progressContent);
 		const progressBar = document.createElement('div');
 		progressBar.className = 'eln-progress-bar';
 		progressContent.appendChild(progressBar);
 		const fill = this.fill = document.createElement('div');
 		fill.className = 'fill';
 		progressBar.appendChild(fill);
 		const progressRate = this.progressRate = document.createElement('div');
 		progressRate.className = 'progress-rate';
 		progressRate.innerHTML = 0;
 		progressContent.appendChild(progressRate);
 		this.domParent.appendChild(mask);
 	}

 	changeValue(v) {
 		this.currentProgress = v;
 	}

 	async run() {
 		while (this.progress <= 100) {
 			if (this.currentProgress <= this.progress) {
 				await this.setProgress(this.progress, this.maxTime / 1000);
 			} else {
 				await this.setProgress(this.progress, 1);
 			}
 			this.progress++;
 		}

 		setTimeout(() => {
 			this.closeProgress();
 		}, 100);

 	}

 	async setProgress(progress, t) {
 		return new Promise(resolve => {
 			setTimeout(() => {
 				this.progress = progress;
 				this.fill.style.width = progress + '%';
 				this.progressRate.innerText = progress + '%';
 				resolve();
 			}, t)
 		})
 	}

 	closeProgress() {
 		this.mask.remove();
 	}
 }