import {Component, ComponentFactoryResolver, OnDestroy, OnInit, ViewChild, ViewContainerRef} from '@angular/core';
import {Globals} from '../../globals';
import {AlertItemComponent} from './alert-item/alert-item.component';
import {ErrorView} from '../../model/error-view';

@Component({
    selector: 'alert',
    templateUrl: './alert.component.html',
    styleUrls: ['./alert.component.css']
})
export class AlertComponent implements OnInit, OnDestroy {

    @ViewChild('container', {read: ViewContainerRef}) container: ViewContainerRef;

    private errorSubscription: any;
    private messageSubscription: any;

    constructor(private globals: Globals,
                private componentFactoryResolver: ComponentFactoryResolver) {
        this.errorSubscription = globals.errorEventEmitter.subscribe(error => {
            this.createAlert(error.message, 'danger');
        });
        this.messageSubscription = globals.messageEventEmitter.subscribe(message => {
            this.createAlert(message, 'info');
        });
    }

    ngOnInit() {
    }

    createAlert(message: string, cls: string) {
        const componentFactory = this.componentFactoryResolver.resolveComponentFactory(AlertItemComponent);
        const alertComponent = this.container.createComponent(componentFactory);
        alertComponent.instance.message = message;
        alertComponent.instance.cls = cls;
        alertComponent.instance.selfRef = alertComponent;
    }

    ngOnDestroy() {
        this.errorSubscription.unsubscribe();
        this.messageSubscription.unsubscribe();
    }

    doStuff(m: string) {
        this.globals.emitMessage(m);
    }
    doErrorStuff(m: string) {
        const err = new ErrorView();
        err.message = m;
        this.globals.emitError(err);
    }
}
