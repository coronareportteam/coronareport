import { Directive, Input, ViewContainerRef, TemplateRef, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';

@Directive({
  selector: '[qroHasRole]'
})
export class HasRoleDirective implements OnInit {
  @Input() qroHasRole: string[];
  isVisible = false;

  constructor(
    private viewContainerRef: ViewContainerRef,
    private templateRef: TemplateRef<any>,
    private userService: UserService) { }

  ngOnInit() {
    if (this.userService.roleMatch(this.qroHasRole)) {
      if (!this.isVisible) {
        this.isVisible = true;
        this.viewContainerRef.createEmbeddedView(this.templateRef);
      }
    } else {
      this.isVisible = false;
      this.viewContainerRef.clear();
    }
  }
}
