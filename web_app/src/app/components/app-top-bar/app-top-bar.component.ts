import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-app-top-bar',
  templateUrl: './app-top-bar.component.html',
  styleUrls: ['./app-top-bar.component.css']
})
export class AppTopBarComponent implements OnInit {

  imageSrc = 'assets/setting.png'  
  imageAlt = 'Settings'

  constructor() { }

  ngOnInit(): void {
  }

}
