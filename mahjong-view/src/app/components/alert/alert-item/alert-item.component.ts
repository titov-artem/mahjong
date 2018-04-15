import {Component, ComponentRef, Input, OnInit} from '@angular/core';

@Component({
    selector: 'alert-item',
    templateUrl: './alert-item.component.html',
    styleUrls: ['./alert-item.component.css']
})
export class AlertItemComponent implements OnInit {

    @Input() message: string;
    @Input() cls: string;
    selfRef: ComponentRef<AlertItemComponent>;

    ngOnInit() {
        setTimeout(() => {
            this.selfRef.destroy();
        }, 3000)
    }

}
